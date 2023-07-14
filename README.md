# JpGui
A GUI program for the jpackage command line program in the Java JDK.

This GUI program will help you to manage jpackage projects.

Some of the features are:

- Can have multiple projects open at once.
- Can manage all three platforms with one GUI. The platforms are Windows, Liunx (like) and Apple Mac
- Each option has a help button.
- A 'New Project Wizard' to help you get started with a project.
- Multiple file and directory selection for options that support it.
- Generates a batch or shell script that can run the jpackage command.
- Can add pre and post run script to be run by the main script.
- Each project is contained in it's own directory structure.
- Can import and export the projects.

This a major change from previous versions. I changed the directory structure and file names generated. :astonished: Sorry.<br>
Please read the JpGui_help.txt for more helpful information.

![jpgui image](src/images/jpgui.png?raw=true)

The GUI will help you create jpackage projects.  I displays command line fields alone with their help text as seen below.

![jpgui image with field help](src/images/jpGui_help.png?raw=true)

You can also generate both Windows and Linux like script as seen below.

![jpgui image of batch script](src/images/jpgui_script.png?raw=true)

When the program starts it creates a new directory called JpGui in your home directory.

The JpGui directory contains the jpgui.ini file and another directory calls projects.<br>
The jpgui.ini file contains program information and the projects directory contains a directory for each project created.<br>
This program can also generate script to run the jpackage command at the command line.

You can create scripts for both Windows and Linux on either the Windows platform or Linux platform.

The program can execute the script but only for the platform this GUI is running on.

If you have any suggestions or questions, please ask.
