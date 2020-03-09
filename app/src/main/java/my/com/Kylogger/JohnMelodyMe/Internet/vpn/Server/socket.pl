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