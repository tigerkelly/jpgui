# JpGui
A GUI program for the jpackage command line program in the Java JDK.<br>
The jpackage program generates an install program that contains the Java runtime for your application.

This GUI program will help you to manage jpackage projects for each of you applications and platforms.

Some of the features are:

- Can have multiple projects open at once.
- Can manage all three platforms with one GUI. The platforms are Windows, Linux (like) and Apple Mac
- Each option has a help button.
- A 'New Project Wizard' to help you get started with a project.
- Multiple file and directory selection for options that support it.
- Generates a batch or shell script that can run the jpackage command.
- Can add pre and post run script to be run by the main script.
- Each project is contained in it's own directory structure.
- Can import and export the projects.

Please read the JpGui_help.txt for more helpful information.

When creating a new JpGui project, use the 'New Project Wizard' menu option, this will get you started.

![jpgui image](src/images/jpgui.png?raw=true)

The GUI will help you create jpackage projects.  Each jpackage option has their own help text, as seen below.

![jpgui image with field help](src/images/jpGui_help.png?raw=true)

You can also generate both Windows and Linux like script as seen below.

![jpgui image of batch script](src/images/jpgui_script.png?raw=true)

When the program starts it creates a new directory called JpGui in your home directory.

The JpGui directory contains the jpgui.ini file and another directory called '**projects**'.<br>
The jpgui.ini file contains program information and the '**projects**' directory contains a directory for each project created.<br>
This program can also generate script to run the jpackage command at the command line.

You can create scripts for both Windows and Linux on either the Windows platform or Linux platform.

The program can execute the script but only for the platform this GUI is running on.

If you have any suggestions or questions, please ask.

Also if you just want the install program created by the Jpackage.<br>
I only have 2 types of install at this time.<br>
Window 64bit intel/amd or Ubuntu 64bit intel/amd<br>
Send me an email if you want a link to one of these files.
