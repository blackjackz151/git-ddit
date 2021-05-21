#!/bin/bash
for i in {1..5}
do
   git ddit new -t title$i -d description$i
done