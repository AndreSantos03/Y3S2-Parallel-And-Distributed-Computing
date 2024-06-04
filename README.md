# CPD Projects

CPD Projects of group T10G15 for the Parallel and Distributed Computing class at FEUP.

## Group Members

1. Afonso Dias (up202006721@up.pt)
2. André Santos (up202108658@up.pt)
3. Pedro Beirão (up202108718@up.pt)

## Project Overview

This repository contains two projects developed for the course of Parallel and Distributed Computing (L.EIC) at the Faculdade de Engenharia da Universidade do Porto.

## Project 1: Performance Evaluation of Matrix Multiplication

### Description

This project evaluates the performance impact of memory hierarchy on processor performance using matrix multiplication as the case study. The project is divided into two parts:

1. **Performance Evaluation of a Single Core:**
   - Implemented the basic matrix multiplication algorithm in C++ and another language (e.g., Java).
   - Collected processing times for matrices ranging from 600x600 to 3000x3000 elements with increments of 400.
   - Implemented an element-by-element multiplication algorithm and collected processing times for the same matrix sizes.
   - Developed a block-oriented algorithm in C++ and measured performance for larger matrices (4096x4096 to 10240x10240) with different block sizes.

2. **Performance Evaluation of a Multi-Core Implementation:**
   - Implemented parallel versions of the matrix multiplication using OpenMP.
   - Evaluated performance metrics such as MFlops, speedup, and efficiency.
   - Analyzed the impact of different parallelization strategies on performance.

### Technologies Used

- **Programming Languages:** C++, Java
- **Libraries:** PAPI (Performance API)
- **Tools:** OpenMP for parallelization

### Learning Outcomes

- Understanding the impact of memory hierarchy on performance.
- Gaining hands-on experience with performance measurement tools.
- Implementing and analyzing parallel algorithms using OpenMP.

### Report

A detailed report was prepared, explaining the algorithm versions, performance metrics, results, analysis, and conclusions.

## Project 2: Distributed Systems Assignment

### Description

This project involves creating a client-server system using TCP sockets in Java for an online multiplayer game. The system supports two modes: simple matchmaking and rank-based matchmaking. It also includes fault tolerance mechanisms to handle broken connections.

### Features

1. **Client-Server Communication:**
   - Implemented using TCP sockets in Java.
   - Supports user authentication and queuing for games.

2. **Matchmaking Modes:**
   - **Simple Mode:** Assigns the first `n` users to a game instance.
   - **Rank Mode:** Forms teams with players of similar skill levels, with level differences relaxed over time.

3. **Fault Tolerance:**
   - Designed a protocol to handle broken connections, allowing users to resume their position in the queue.

4. **Concurrency:**
   - Ensured no race conditions using custom implementations of synchronization mechanisms.
   - Utilized Java virtual threads to minimize thread overheads.
   - Ensured the system is robust against slow clients.

5. **Game Implementation:**
   - Implemented a multiplayer Wordle-like game.
   - One player chooses the word while others guess it.

### Technologies Used

- **Programming Language:** Java (SE 21)
- **Libraries:** java.util.concurrent.locks for custom synchronization

### Learning Outcomes

- Developing a distributed system using TCP sockets.
- Implementing robust concurrency control mechanisms.
- Ensuring fault tolerance in a client-server architecture.

### How to Run

#### Server

1. Compile the server and game classes:
   ```bash
   javac Auth.java RankedQueue.java Server.java Game.java Player.java
    ```

2. Run the server:
    ```bash
    java Server {PORT} {NUMBER OF PLAYERS}
    ```

**Client**

1. Compile the client class:

```bash
javac Player.java
```

2. Run the client:

```bash
java Player {SERVER IPADDRESS} {PORT}
```

## Conclusion

These projects provided practical experience in performance evaluation, parallel programming, and distributed systems. The first project enhanced our understanding of the impact of memory hierarchy and parallelization on performance, while the second project allowed us to design and implement a robust client-server system with advanced concurrency and fault tolerance features.

---------

**Course**: Licenciatura em Engenharia Informática e Computação
**Institution**: Faculdade de Engenharia da Universidade do Porto
**Year**: 2023/2024

