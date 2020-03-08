

#!/usr/bin/perl

use strict;
use warnings;

use IO::Socket::INET;

# Send data immediately without buffering
$| = 1;

my ($socket,$data);

#  Create a new UDP socket
$socket = new IO::Socket::INET (
    LocalPort => 3671,
    Proto        => 'udp'
) or die "ERROR creating socket : $!\n";

my ($datagram,$flags);
while (1) {
    $socket->recv($datagram,42,$flags);
    print "Received datagram from ", $socket->peerhost,
                  ", flags ", $flags || "none", ": $datagram\n";
}

$socket->close();

1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28

#!/usr/bin/perl

use strict;
use warnings;

use IO::Socket::INET;

# Send data immediately without buffering
$| = 1;

my ($socket,$data);

#  Create a new UDP socket
$socket = new IO::Socket::INET (
    LocalPort => 3671,
    Proto        => 'udp'
) or die "ERROR creating socket : $!\n";

my ($datagram,$flags);
while (1) {
    $socket->recv($datagram,42,$flags);
    print "Received datagram from ", $socket->peerhost,
                  ", flags ", $flags || "none", ": $datagram\n";
}

$socket->close();