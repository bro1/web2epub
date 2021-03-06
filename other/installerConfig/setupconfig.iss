; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{B02497BC-BB51-457A-B2D7-8B25FD3292F3}
AppName=web2epub
AppVersion=0.1
;AppVerName=web2epub 0.1
AppPublisher=Linas Jakucionis
AppPublisherURL=http://www.github.com/bro1/web2epub
AppSupportURL=http://www.github.com/bro1/web2epub
AppUpdatesURL=http://www.github.com/bro1/web2epub
DefaultDirName={pf}\web2epub
DefaultGroupName=web2epub
DisableProgramGroupPage=yes
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "web2epub.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\..\web2epub.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "..\..\lib\*"; DestDir: "{app}\lib\"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "..\..\build\installer\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\web2epub"; Filename: "{app}\web2epub.exe"
Name: "{commondesktop}\web2epub"; Filename: "{app}\web2epub.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\web2epub.exe"; Description: "{cm:LaunchProgram,web2epub}"; Flags: nowait postinstall skipifsilent


