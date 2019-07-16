
k - number of servers
r - number of requests
d - d arbitrary number of dimensions with their length
spostype - starting position of servers type
reqstype - types of requests or rather mode of functioning

name:k:r:d1 d2 ... dn-1 dn:spostype:reqstype


spostype examples:
INLINE 3,3 3,3 3,3 (takes the numbers as input points)
INLINE 3,3,3 3,3,3 3,3,3
FILE filename.txt (takes the inside of the file as input points, inside is just a line with 3,3 3,3 3,3 similar to INLINE above)
RANDOM (points appear completely randomly)

reqstype examples:
All the spostype examples +
WAVE (points appear in a wave like shape, see my Thesis for details)
CORNER (point appear first in a corner than they spread out see my thesis)
