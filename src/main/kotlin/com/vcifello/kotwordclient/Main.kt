package com.vcifello.kotwordclient

fun main(args: Array<String>) = CliCommand().main(args)
















//suspend fun main(args: Array<String>) {
//    println("Hello World!")
//
//
//    val fetcher = Fetcher("1wohJzZLHovwg1UYaXzEXaGI.hYg1hdkH.BvY0Qc8yXC8ifQLFKflZTlJZj4VgIqFBjOoea6bgYnRxckKfk3fLpwyGx9OToAy29WWK3c3XEgatAPH6yIz0L3xDTQO38FS1^^^^CBQSLwjNmOqmBhDKoOqmBhoSMS35FtILBIFlAXAm1JKbKJx_IN65wR8qAgACONeglv0EGkBvS6eRY1RKeW0qMXFM7kG6QK1av-2H1AqazZ0n7K29gQpGDOmPeCuJ5PLqTz9otisO2zF5KRAbeTXOxewhJ5sI")
//
//    val inputDate = "2023-08-20"
//    val pd = LocalDate.parse(inputDate)
//    val ds  = pd.toString()
//
//    println(ds)
//
//    println(Formats.PDF.format)
//    println(Formats.PDF.name)
//    val (bytes, filename) = fetcher.getPuzzleAsBytes(pd, Formats.PUZ, Sources.NEWYORKTIMESDAILY )
//    println(filename)
//    println(bytes.size)
//    File("${filename}.${Formats.PUZ.format}").writeBytes(bytes)
//
//
//    // Try adding program arguments via Run/Debug configuration.
//    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
//    println("Program arguments: ${args.joinToString()}")
//}