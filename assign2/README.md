# CPD Projects

CPD Projects of group T10G15.

Group members:

1. Afonso Dias (up202006721@up.pt)
2. André Santos  (up202108658@up.pt)
3. Pedro Beirão (up202108718@up.pt)

## Description

Multiplayer Wordle-like game. 1 player chooses the word while the others try to guess it.

Implemented in Java 21 using multithreading.

## How to run

With Java 21, run ``javac Auth.java  RankedQueue.java Server.java Game.java Player.java``

On the server run ``java Server {PORT} {NUMBER OF PLAYERS}``

On each player run ``java Player {SERVER IPADRESS} {PORT}``
