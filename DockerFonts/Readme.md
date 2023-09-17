# Docker font issues

pdfbox was 
- rebuilding cache
- warning that fallback fonts were being used

I was unable to figure out how to 'install' the fonts in the container.  

## Solution/Workaround

Copy locally installed font files into /usr/share/fonts/myfonts  

Let pdfbox build cache, copy from docker container, save to project, and finally copy to root!

