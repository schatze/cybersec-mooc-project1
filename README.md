# cybersec-mooc-project1

LINK: https://github.com/schatze/cybersec-mooc-project1
Open the project in NetBeans for TMC and run it. Note that it is configured to run on port 8081, you can change this in “ServletConfig.java”.

## FLAW 1: Broken Authentication
### Description:
The accounts with admin access have usernames that can easily be guessed. All the passwords for users including the ones for admin accounts are very insecure. Using OWASP Zap and a password list lets you crack all the passwords in a matter of minutes.
### Fix:
Disabling or renaming the accounts like “admin”, “sysadmin” and “root” and using secure passwords generated with a tool like https://passwordsgenerator.net/ will help a lot. Adding limits to login attempts and/or a timer between login attempts will improve the situation even more. Limiting logins could be done on a username basis or with the IP address of clients.

## FLAW 2: Broken Access Control
### Description:
Although the link to the admin page on the main page is hidden for regular users and the text “You do not have admin access” is shown instead, the admin page can still be opened by directly browsing to /admin. Admins have a form for adding users on the admin page, which is hidden for regular users, but it can still be used for example by pasting some javascript in the browsers console. Here is an example script that adds a user without admin  access:
var url = "http://localhost:8081/user";
var params = "password=test&username=test";
var xhr = new XMLHttpRequest();
xhr.open("POST", url, true);
xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
xhr.send(params);
### Fix:
Checking the users access level must be done before any privileged content is served or any privileged action is completed. There is an example of how to fix the issue in “DefaultController.java” on lines 101 and 102. Uncommenting these lines will fix the broken access control of viewing the admin page. Lines 70 to 74 show an example of a fix for the broken access control of adding users.

## FLAW 3: Sensitive Data Exposure
### Description:
Usernames and their password hashes are shown on the admin page, which is unnecessary and only begs for someone without privileges to grab them. Although the passwords are hashed, the algorithm used is md5 and no salt is used. This makes it trivial to crack the hashes. Because the passwords used are extremely insecure, the hashes have long been cracked and an online tool will give the passwords in cleartext instantly.
### Fix:
There is no need to show the hashes of passwords on the admin page, this should be removed. At least using a salt in the hashing algorithm will help, but md5 has been found unfit as a cryptographic hashing algorithm due to numerous vulnerabilities. Using a good cryptographic hashing algorithm like bcrypt will remedy this vulnerability.

## FLAW 4: Stored XSS
### Description:
The names of users are not directly retrieved from the authentication service, but there is a hidden field in the form for the username. Although the message part of the messageboard functionality of the webapp is sanitized, the username field is not sanitized and allows for a stored Cross-Site Scripting attack. It is trivial to edit the HTML in the browser and inject malicious javascript in the username field of the form. Because the injected code is stored on a page, anyone who views the message list will execute the script. Although the database has a limit for the length of the message, executing a large script is still possible by loading it from a remote address.
### Fix:
Retrieving the username of the poster from a hidden field in the form is not a good idea. Trusting anything that a user could tamper with is a bad idea. There is an example on line 56 in “DefaultController.java” that shows how to get the posters username directly from the authentication service. The reason there is an XSS vulnerability in the first place can be seen on line 13 in “done.html”. The tag th:utext stands for unescaped text and changing it to th:text, like in the message part, will remove this vulnerability.


## FLAW 5: No Logging
### Description:
There is close to no logging in the app at all. A few debug strings are sent to stdout but nothing is stored to disk. When an attack would occur, there would be no way of tracing what had happened or who could be behind the attack. Because information about logins is not logged, detecting a brute-force attack or unauthorized access to privileged accounts is near to impossible.
### Fix:
Adding more debug strings about login data and other activity and piping stdout to a file would be a bare minimum solution. Implementing a dedicated logging system that keeps track of users, their IP’s and actions made in the app and logs them to disk would solve this problem.

## (FLAW 6): CSRF
Although not in the 2017 top 10, CSRF forgery is listed as noteworthy in the OWASP report. CSRF tokens in forms have been disabled in the webapp but commenting line 23 in “SecurityConfiguration.java” will enable them.
