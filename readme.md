# Class Search API

Class Search API to query the course catalog in real time for course information and availability.

### Generate Keys

HTTPS is required for Web APIs in development and production. Use `keytool(1)` to generate public and private keys.

Generate key pair and keystore:

    $ keytool \
        -genkeypair \
        -dname "CN=Jane Doe, OU=Enterprise Computing Services, O=Oregon State University, L=Corvallis, S=Oregon, C=US" \
        -ext "san=dns:localhost,ip:127.0.0.1" \
        -alias doej \
        -keyalg RSA \
        -keysize 2048 \
        -sigalg SHA256withRSA \
        -validity 365 \
        -keystore doej.keystore

Export certificate to file:

    $ keytool \
        -exportcert \
        -rfc \
        -alias "doej" \
        -keystore doej.keystore \
        -file doej.pem

Import certificate into truststore:

    $ keytool \
        -importcert \
        -alias "doej" \
        -file doej.pem \
        -keystore doej.truststore

## Gradle

This project uses the build automation tool Gradle. Use the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) to download and install it automatically:

    $ ./gradlew

The Gradle wrapper installs Gradle in the directory `~/.gradle`. To add it to your `$PATH`, add the following line to `~/.bashrc`:

    $ export PATH=$PATH:/home/user/.gradle/wrapper/dists/gradle-2.4-all/WRAPPER_GENERATED_HASH/gradle-2.4/bin

The changes will take effect once you restart the terminal or `source ~/.bashrc`.

## Tasks

List all tasks runnable from root project:

    $ gradle tasks

### IntelliJ IDEA

Generate IntelliJ IDEA project:

    $ gradle idea

Open with `File` -> `Open Project`.

### Configure

Copy [configuration-example.yaml](configuration-example.yaml) to `configuration.yaml`. Modify as necessary, being careful to avoid committing sensitive data.

### Build

Build the project:

    $ gradle build

JARs [will be saved](https://github.com/johnrengelman/shadow#using-the-default-plugin-task) into the directory `build/libs/`.

### Run

Run the project:

    $ gradle run

## Contrib Files

Any code that contains intellectual property from a vendor should be stored in Github Enterprise instead of public Github. Make the name of the contrib repo in Github Enterprise follow this format using archivesBaseName in gradle.properties.

    archivesBaseName-contrib

Set the value of getContribFiles to yes in gradle.properties.

    getContribFiles=yes

Also set the value of contribCommit to the SHA1 of the desired commit to be used from the contrib repository.

    contribCommit={SHA1}
    
Files in a Github Enterprise repo will be copied to this directory upon building the application.

    gradle build

Contrib files are copied to:

    /src/main/groovy/edu/oregonstate/mist/contrib/
    
## Base a New Project off the Skeleton

Clone the skeleton:

    $ git clone --origin skeleton git@github.com:osu-mist/web-api-skeleton.git my-api
    $ cd my-api

Rename the webapiskeleton package and SkeletonApplication class:

    $ git mv src/main/groovy/edu/oregonstate/mist/webapiskeleton src/main/groovy/edu/oregonstate/mist/myapi
    $ vim src/main/groovy/edu/oregonstate/mist/myapi/SkeletonApplication.class

Update gradle.properties with your package name and main class.

Replace swagger.yaml with your own API specification.

Update configuration-example.yaml as appropriate for your application.

Update the resource examples at the end of this readme.

## Base an Existing Project off the Skeleton

Add the skeleton as a remote:

    $ git remote add skeleton git@github.com:osu-mist/web-api-skeleton.git
    $ git fetch skeleton

Merge the skeleton into your codebase:

    $ git checkout feature/abc-123-branch
    $ git merge skeleton/master
    ...
    $ git commit -v


## Incorporate Updates from the Skeleton

Fetch updates from the skeleton:

    $ git fetch skeleton

Merge the updates into your codebase as before.
Note that changes to CodeNarc configuration may introduce build failures.

    $ git checkout feature/abc-124-branch
    $ git merge skeleton/master
    ...
    $ git commit -v


## Resources

The Web API definition is contained in the [Swagger specification](swagger.yaml).

The following examples demonstrate the use of `curl` to make authenticated HTTPS requests.

### GET /api/v1/

This resource returns build and runtime information:

    $ echo -n "username:password" | base64
    dXNlcm5hbWU6cGFzc3dvcmQ=
    $ openssl s_client -connect localhost:8080 -CAfile doej.cer 
    CONNECTED(00000004)
    depth=0 C = US, ST = Oregon, L = Corvallis, O = Oregon State University, OU = Enterprise Computing Services, CN = Jane Doe
    verify error:num=18:self signed certificate
    verify return:1
    depth=0 C = US, ST = Oregon, L = Corvallis, O = Oregon State University, OU = Enterprise Computing Services, CN = Jane Doe
    verify return:1
    ---
    Certificate chain
     0 s:/C=US/ST=Oregon/L=Corvallis/O=Oregon State University/OU=Enterprise Computing Services/CN=Jane Doe
       i:/C=US/ST=Oregon/L=Corvallis/O=Oregon State University/OU=Enterprise Computing Services/CN=Jane Doe
    ---
    Server certificate
    -----BEGIN CERTIFICATE-----
    MIIDvzCCAqegAwIBAgIEHOuDIzANBgkqhkiG9w0BAQsFADCBjzELMAkGA1UEBhMC
    VVMxDzANBgNVBAgTBk9yZWdvbjESMBAGA1UEBxMJQ29ydmFsbGlzMSAwHgYDVQQK
    ExdPcmVnb24gU3RhdGUgVW5pdmVyc2l0eTEmMCQGA1UECxMdRW50ZXJwcmlzZSBD
    b21wdXRpbmcgU2VydmljZXMxETAPBgNVBAMTCEphbmUgRG9lMB4XDTE1MTAyNzE5
    MDMxOFoXDTE2MDEyNTE5MDMxOFowgY8xCzAJBgNVBAYTAlVTMQ8wDQYDVQQIEwZP
    cmVnb24xEjAQBgNVBAcTCUNvcnZhbGxpczEgMB4GA1UEChMXT3JlZ29uIFN0YXRl
    IFVuaXZlcnNpdHkxJjAkBgNVBAsTHUVudGVycHJpc2UgQ29tcHV0aW5nIFNlcnZp
    Y2VzMREwDwYDVQQDEwhKYW5lIERvZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC
    AQoCggEBAKJUuSjl/TjFYtkh0/5cb+TBMvhndKl+lIe75KB81PoXQpkePz2456Rv
    4KXyM94Dyg+i+eccBC2RNXVTrq0bXqQ/utWRBpEn6IRLFobA29lH9ZoDhhHe52kK
    tP1mTFCCi/3GCGTZXuj65DCyLI5gyT2Cyjjf8rpXdSDXhHATRdpdw474JcngMbIo
    8JtgsHp6b5X87FfZGrKAOwQLp+ifzBU5sVK+mhi1pwlyzBkPx9Ma/ctrVR4NEJGX
    z/NJegEE4o3FVVHJnOiFZWfYdYUqQi8WtNaG6oRdwsBS3nfdD/EIum0j5EMOFxji
    jaCNYIzkZFGhlSqPrcHQ8pPcnVoi53MCAwEAAaMhMB8wHQYDVR0OBBYEFKVL9W4P
    /fUH1JfWkMu1Ty+PZF5gMA0GCSqGSIb3DQEBCwUAA4IBAQBetD1CpwAThmSxTkX+
    sowZ/vvhKGiYI+3PIKCasXYw37Kdg15xfN1LJQVpgKhlvT5U6i03dSg0ZXUhpwLb
    LWsI6Heq5y549+4HJyhqGTyec5HCxFgLAvGh4Tc5bD/zrEDi366YPrxj7nzapfge
    S3xhvF2V0VuS0LVZ0cwENKzVuz1FSNyZG6VEQ1slGuUYJ+laRZ5CPBo5d5KfdIKG
    8gVTetwacPP8fNNt2IOg4DledSzFn2ahLxXtyzXvu2gjFukfVC0bR82KuYZnJQIu
    ezfIeCrkHo3HUX7KfDbFGnjtOXN1B175cAY3zZb3IKUQMQoy+MPJBoC8YU25LL6n
    K4vv
    -----END CERTIFICATE-----
    subject=/C=US/ST=Oregon/L=Corvallis/O=Oregon State University/OU=Enterprise Computing Services/CN=Jane Doe
    issuer=/C=US/ST=Oregon/L=Corvallis/O=Oregon State University/OU=Enterprise Computing Services/CN=Jane Doe
    ---
    No client certificate CA names sent
    ---
    SSL handshake has read 1567 bytes and written 544 bytes
    ---
    New, TLSv1/SSLv3, Cipher is ECDHE-RSA-AES128-SHA256
    Server public key is 2048 bit
    Secure Renegotiation IS supported
    Compression: NONE
    Expansion: NONE
    SSL-Session:
        Protocol  : TLSv1.2
        Cipher    : ECDHE-RSA-AES128-SHA256
        Session-ID: 562FCC0D8492CDA3515903B2B0D25D20D7EA9BBF7F8C21F84FBFA7EC294B98A3
        Session-ID-ctx: 
        Master-Key: 264ABBA6CCE9F6516E0709201011219E8BBD08F90B08CF732E26951A91C0BA148B844FCAFA112304269C4781D2851462
        Key-Arg   : None
        PSK identity: None
        PSK identity hint: None
        SRP username: None
        Start Time: 1445973005
        Timeout   : 300 (sec)
        Verify return code: 18 (self signed certificate)
    ---
    GET /api/v1 HTTP/1.0
    Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
    
    HTTP/1.1 200 OK
    Date: Tue, 27 Oct 2015 19:10:23 GMT
    Content-Type: application/json
    Content-Length: 111
    
    {"name":"courses-api","time":1467959701699,"commit":"4ba4715","documentation":"swagger.yaml"}closed

### GET /api/v1/courses?term={term}&subject={subject}&courseNumber={courseNumber}&q={searchQuery}

This resource returns an array of objects representing the courses matching the search query:

    $ curl \
     --insecure \
     --key doej.cer \
     --user "username:password" \
     https://localhost:8080/api/v1/courses?term=201701&subject=CS&courseNumber=101
     {"links":{},"data":[{"id":"10383","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"10383","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":4,"enrollment":140,"maximumEnrollment":140,"openSection":"false","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Lecture","section":"001","status":"Closed","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":0,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1300","endTime":"1350","building":"LINC","buildingName":"Learning Innovation Center","room":"200","campus":"C","campusDescription":" Oregon State - Corvallis","monday":true,"tuesday":false,"wednesday":true,"thursday":false,"friday":true,"saturday":false,"sunday":false}]},"links":null},{"id":"10388","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"10388","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"006","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":3,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"0900","endTime":"0950","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"10387","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"10387","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"007","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":1,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1000","endTime":"1050","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"10386","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"10386","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"008","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":2,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1100","endTime":"1150","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"10384","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"10384","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"009","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1200","endTime":"1250","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"11454","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"11454","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"010","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1300","endTime":"1350","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"11367","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"11367","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"011","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1400","endTime":"1450","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"11368","type":"course","attributes":{"campusDescription":" Oregon State - Corvallis","courseNumber":"101","crn":"11368","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":0,"enrollment":20,"maximumEnrollment":20,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Laboratory","section":"012","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":10,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":"1500","endTime":"1550","building":"MCC","buildingName":"Milne Computer Center","room":"130","campus":"C","campusDescription":" Oregon State - Corvallis","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"15322","type":"course","attributes":{"campusDescription":"Ecampus-Distance Education-LD","courseNumber":"101","crn":"15322","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":4,"enrollment":56,"maximumEnrollment":100,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"WWW","section":"400","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":5,"waitCount":0,"faculty":[{"displayName":"Doe, John","primaryFaculty":true}],"meetingTimes":[{"startTime":null,"endTime":null,"building":null,"buildingName":null,"room":null,"campus":null,"campusDescription":null,"monday":false,"tuesday":false,"wednesday":false,"thursday":false,"friday":false,"saturday":false,"sunday":false}]},"links":null},{"id":"18806","type":"course","attributes":{"campusDescription":"Oregon State - Cascades","courseNumber":"101","crn":"18806","sectionTitle":null,"creditHourHigh":4,"creditHourLow":0,"creditHours":4,"enrollment":1,"maximumEnrollment":40,"openSection":"true","termStartDate":"2016-09-21","termEndDate":"2016-12-02","termWeeks":"10","scheduleTypeDescription":"Lecture","section":"501","status":"Open","subject":"CS","subjectCourse":"CS101","subjectDescription":"Computer Science","term":"201701","termDescription":"Fall 2016","waitCapacity":0,"waitCount":0,"faculty":[{"displayName":"Doe, Jane","primaryFaculty":true}],"meetingTimes":[{"startTime":"1300","endTime":"1450","building":"TYKH","buildingName":"Tykeson Hall (Bend)","room":"205","campus":"B","campusDescription":"Oregon State - Cascades","monday":false,"tuesday":true,"wednesday":false,"thursday":true,"friday":false,"saturday":false,"sunday":false}]},"links":null}]}

Courses matches term, course number, subject and query. An empty array is returned if no courses match:
    $ curl \
      --insecure \
      --user "username:password" \
      'https://localhost:8080/api/v1/courses/?term=201708&subject=XX'
    {"links":null,"data":[]}

An error is returned if the request is invalid:

    $ curl \
      --insecure \
      --user "username:password" \
      'https://localhost:8080/api/v1/courses/?term=xx'
    {"status":400,"developerMessage":"term should be a 6 digit code","userMessage":"Bad Request - the application submitted invalid data. Please try again.","code":1400,"details":"https://developer.oregonstate.edu/documentation/error-reference#1400"}

