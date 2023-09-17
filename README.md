# Kotword Client (console)
Console application frontend to download and email puzzles.  

Wraps [kotwordfetcher]("linkhere") library which uses ktor clients to fetch puzzle sources and convert them into the desired formats with the **absolutely amazing** [kotwords](https://github.com/jpd236/kotwords) library. 



This was part of an attempt to learn about programming and kotlin. Most importantly, this provides my wife with NYT Daily puzzles delivered to her doorstep in puz format.

## Usage
Usage: cli [<options>]

Options:

-D                       Download only. Suppresses default emails  
-n, --nyts=<text>        NYT-S token is required. May set in config.json  
-d, --date=<date>        Must be a valid date 'yyyy-MM-dd'. Default is today  
-s, --source=(nyt|nytm)  Specify puzzle source. Default is 'nyt'  
-f, --format=(puz|pdf|jpuz|ipuz) 
                         Specify puzzle format. Default is 'puz'  
-e, --email=<text>...    Email your downloaded puzzle!  
-v, --verbose            Writes out debug info.  
-h, --help               Show this message and exit  

## config.json  
config.json in same folder as jar or folder where docker is run  

{  
"nyts": "YOUR_TOKEN_HERE",  
"email": ["DEFAULT" , "EMAILS", "HERE"],  
"sender":  "YOUR_GMAIL_HERE@gmail.com",  
"senderPass": "YOUR_GMAIL_APPLICATION_PASSWORD_HERE"
}

## Downloaded Puzzles
These will be saved in same folder as the jar:

//Puzzles/2023/nyt/2023-01-01-filename.ext

## Docker
There is a simple Dockerfile that can containerize the shadowJar.  

Mount a host folder for the Puzzles folder.  
Mount a config.json to control the default/required values.  

Example usage:  

docker build -t kotwordclient

Synology build
docker buildx build --platform=linux/amd64,linux/arm64 -t vcifello/kotwordclient --push .

docker run --rm --volume /Users/vincentcifello/kotwords/Puzzles:/usr/bin/runner/Puzzles --volume /Users/vincentcifello/kotwords/config.json:/usr/bin/runner/config.json kotwordclient java -jar run.jar -h

### TODO

- Add more sources
- Consider option to specify output location
- Error checking (docker run --rm --volume /volume1/Crosswords/kotwords/Puzzles:/usr/bin/runner/Puzzles --volume /volume1/Crosswords/kotwords/config.json:/usr/bin/runner/config.json vcifello/kotwordclient java -jar run.jar -d 2023-09-18
  Exception in thread "main" com.vcifello.kotwordfetcher.Http$HttpException: HTTP GET error code 404 Not Found from URL: https://www.nytimes.com/svc/crosswords/v6/puzzle/daily/2023-09-18.json
  at com.vcifello.kotwordfetcher.Http.fetchAsString(Http.kt:37))
- Consider implementing nyt-s token fetching from username and password.








