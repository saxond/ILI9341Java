# ILI9341Java

This is a WIP java port of [Adafruit_Python_ILI9341](https://github.com/adafruit/Adafruit_Python_ILI9341/blob/master/Adafruit_ILI9341/ILI9341.py).

## Why did I do this?

Very reasonable question.  I bought a PiStorms Lego Mindstorms Raspberry Pi shield which 
uses a ILI9341 display.  All of the software provided with that thing is written in Python
and I prefer to write Java so I started porting.

## Current state

It seems like commands (gpio low state on the dc pin) aren't working.  The display initialization
code isn't functioning.  If you run another program that initializes the display, then run this library,
it writes something to the display.  