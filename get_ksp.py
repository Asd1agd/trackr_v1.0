import urllib.request
import re

url = "https://repo1.maven.org/maven2/com/google/devtools/ksp/symbol-processing-api/maven-metadata.xml"
try:
    response = urllib.request.urlopen(url)
    data = response.read().decode('utf-8')
    versions = re.findall(r'<version>(2\.[0-9]+.*)</version>', data)
    print("Found:", versions[-5:] if versions else "None")
except Exception as e:
    print(e)
