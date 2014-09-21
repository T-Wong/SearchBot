BingBot
=======

BingBot is a simple script with GUI that automates the earning of points for Bing Rewards. I tried to make this program very simple so that any person can pick it up and instantly use it without having to know a lot about Bing Rewards.

![alt tag](https://raw.github.com/T-Wong/BingBot/master/GUI.png)

## Features
* Allows for up to 5 accounts at a time (not Facebook accounts)
* A ~25,000 search list that contains many commonly searched for (contains NSFW language)
* Uses PhantomJS and Seleneium to drive a hidden browser to earn points
* Go to this project [here] (https://github.com/T-Wong/SearchList/tree/master) if you want to have a large list millions of search terms that I have compiled
* Encrypts and saves login information on local machine
* Detects how many points needed for both mobile searching and desktop searching
* Earns the "Earn and Explore" points
* Variable search delay that can be changed in the code, default is 10-30 seconds
* Searches for web, images, and videos for desktop searching (image and video searches don't register for points on mobile for some reason)
* Emulates an iPhone to obtain mobile searches

## Requirements
* Modern version of Mozilla Firefox
* Java 1.7 or 7 JRE

## Installation
1. Download BingBot.jar in the master branch or click [here] (https://github.com/T-Wong/BingBot/raw/master/BingBot.jar).
2. Run the downloaded file.
3. Done.

or you can compile it yourself if you don't trust my pre-compiled version.

## License
The MIT License (MIT)

Copyright (c) 2014 Tyler Wong

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.