package com.vcifello.kotwordclient

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import com.vcifello.kotwordfetcher.Fetcher
import com.vcifello.kotwordfetcher.sources.Formats
import com.vcifello.kotwordfetcher.sources.Sources
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.File
import java.lang.Exception
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.io.path.*

class CliCommand : CliktCommand(help = " Download and Email puzzles!!"){

    init {
        completionOption() //?
        //unable to differentiate cli, config, none bc config gets loaded if option NOT called
        context {
            valueSources(
                JsonValueSource.from("config.json")
            )
        }
    }

    //this is necessary since the default emails are loaded automatically even when --email not set
    // this flag will allow only download
    private val downloadOnly: Boolean by option("-D", help = "Download only. Suppresses default emails").flag()

    private val nyts: String by option("-n", "--nyts", help = "NYT-S token is required. May set in config.json").required()

    private val date by option("--date", "-d", help = "Must be a valid date 'yyyy-MM-dd'. Default is today").convert("date"){
        validDateOrNull(it) ?: fail("Must be a valid date 'yyyy-MM-dd' ")
    }.default(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    private val source by option("--source", "-s", help = "Specify puzzle source. Default is 'nyt'").enum<Sources>(ignoreCase = true) { it.keyName }.default(
        Sources.NEWYORKTIMESDAILY)

    private val format by option("--format", "-f", help = "Specify puzzle format. Default is 'puz'").enum<Formats>(ignoreCase = true) {it.format}.default(
        Formats.PUZ)

    private val email: List<String>? by option("--email", "-e", help = "Email your downloaded puzzle!")
        .varargValues()
        //.multiple()
        .validate {
            it.forEach{
                require(isValidEmail(it)) {
                    "Email must be valid: $it"
                }
            }
        }

    private val verbose: Boolean by option("--verbose", "-v", help = "Writes out debug info.").flag()



    override fun run() {

        loadConfig()

        if (verbose) {

            println(cfg)
            println("token: $nyts")
            println("--date: $date")
            println("--source: $source")
            println("--format: $format.format")
            println("--email $email")
            println("downloadOnly: $downloadOnly")
    }


        val fetcher = Fetcher(nyts)

        runBlocking {

            val (bytes, filename) = fetcher.getPuzzleAsBytes(date,format,source)

            val savePath = getSaveFilePath(date, source, filename, format)

            if (verbose){ println(savePath) }

            File(savePath).writeBytes(bytes)

            println("Puzzle saved!\n$savePath")

            if (!downloadOnly && !email.isNullOrEmpty()) {
                 sendMail(savePath, "$filename.${format.format}", createEmailSubject(source,date), email!!, verbose)
            }
        }
    }
}

private fun ensureSaveDirectory(date: LocalDate, sourceKeyName: String) : String{
    return Path("Puzzles", date.year.toString(), sourceKeyName).createDirectories().absolutePathString()
}

private fun getSaveFilePath(date: LocalDate, source: Sources, filename: String, format: Formats): String {

    //Puzzles/2023/nyt/2023-01-01-filename.ext
    val dir = ensureSaveDirectory(date, source.keyName)

    return "$dir/$date-${filename}.${format.format}"

}

private fun sendMail(filePath: String, puzzleName: String, subject: String, emails : List<String>, debug : Boolean) {
    val userName =  cfg.sender
    val password =  cfg.senderPass
    // FYI: passwords as a command arguments isn't safe
    // They go into your bash/zsh history and are visible when running ps

    val text = "Enjoy your puzzle...\n\n"

    val props = Properties()
    putIfMissing(props, "mail.smtp.host", "smtp.gmail.com")
    putIfMissing(props, "mail.smtp.port", "587")
    putIfMissing(props, "mail.smtp.auth", "true")
    putIfMissing(props, "mail.smtp.starttls.enable", "true")

    val session = Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(userName, password)
        }
    })

    session.debug = debug

    try {
        val mimeMessage = MimeMessage(session)
        mimeMessage.setFrom(InternetAddress(userName))
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails.joinToString(separator = ","), false))
        //mimeMessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailCC, false))
        //mimeMessage.setText(text)
        mimeMessage.subject = subject
        mimeMessage.sentDate = Date()


        var messageBodyPart: BodyPart = MimeBodyPart()

        messageBodyPart.setText(text);
        val multipart: Multipart = MimeMultipart()
        multipart.addBodyPart(messageBodyPart)

        // Second part is attachment
        // Second part is attachment
        messageBodyPart = MimeBodyPart()
        //val filename = "abc.txt"
        val source: DataSource = FileDataSource(filePath)
        messageBodyPart.setDataHandler(DataHandler(source))
        messageBodyPart.setFileName(puzzleName)//Path(filePath).fileName.toString())
        multipart.addBodyPart(messageBodyPart)

        // Send the complete message parts

        // Send the complete message parts
        mimeMessage.setContent(multipart)


        val smtpTransport = session.getTransport("smtp")
        smtpTransport.connect()
        smtpTransport.sendMessage(mimeMessage, mimeMessage.allRecipients)
        smtpTransport.close()

        println("Emails sent!\n${emails.joinToString()}")

    } catch (messagingException: MessagingException) {
        messagingException.printStackTrace()
        println("Error sending emails")
    }
}
private fun putIfMissing(props: Properties, key: String, value: String) {
    if (!props.containsKey(key)) {
        props[key] = value
    }
}

private fun createEmailSubject(source: Sources, date: LocalDate) : String
{
    return "Puzzle Alert! $date ${source.sourceName}"
}


private fun  validDateOrNull(date: String) : LocalDate? {
    return try{
        LocalDate.parse(date)
    }catch(e : Exception){
        null
    }
}

private fun isValidEmail(email: String) : Boolean {

    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    val res = email.matches(emailRegex.toRegex())
    return res
}