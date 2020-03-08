require 'socket'

BUFFER_SIZE = 1024

socket = UDPSocket.new
socket.bind('219.100.37.96', 8000)

loop do
  message, sender = socket.recvfrom(BUFFER_SIZE)

  port = sender[1]
  host = sender[2]

  socket.send(message.upcase, 0, host, port)
end