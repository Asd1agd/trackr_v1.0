#!/bin/bash
curl -s "https://search.maven.org/solrsearch/select?q=g:com.google.devtools.ksp+a:symbol-processing-api&rows=20&wt=json" | grep -o '2\.3\.20-[0-9.]*'
