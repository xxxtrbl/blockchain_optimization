# Design and Implementation of Blockchain Access Optimization Based on Caching Mechanism
## Thinking
The focus of this project is to optimize the underlying structure of the blockchain. Therefore, the consensus layer of the blockchain has been removed, with a focus solely on the design and optimization of the blockchain's underlying architecture. Here, we are not implementing "distributed" but instead concentrating solely on the data storage aspect.
## 1. Traditional Blockchain System
### Traditional Blockchain Architecture
As shown in the diagram, the incomplete traditional blockchain architecture has its consensus layer removed in this project, focusing solely on the underlying structure of the blockchain system.<br/>
  <img width="500" alt="blocksearch" src="pics/oldArchitecture.png">

**Storage Layer**：The storage layer is primarily used for storing content related to transactions. In this project, RocksDB is employed to store the data of blocks and transaction content.<br/>

**Data Layer**：The data layer is mainly responsible for handling various types of data in transactions, such as packaging data into blocks, maintaining blocks in a chained structure, and performing encryption and hashing calculations on the contents of blocks.
The digital signature of block contents, along with adding a timestamp imprint, involves constructing transaction data into a Merkle tree and computing the hash value of the Merkle tree root node, among other tasks.<br/>

**Internet Layer**：The network layer primarily provides the underlying support for achieving consensus and data communication. In a blockchain, each node serves as both the sender and receiver of data. In this project, we disregard communication between nodes in the blockchain network and focus solely on the underlying structure of the blockchain, thus making the project's structure clearer and more organized.<br/>

**Application layer**：The application layer mainly includes applications for users to transmit data (data on-chain) and perform data queries (such as blockchain traceability and information retrieval).<br/>
### Traditional Blockchain Storage Method
#### Data layer (block) design
<img width="500" alt="blocksearch" src="pics/block.svg">
A Block consists of BlockHeader, BlockBody, and BlockHash. <br/>
BlockHeader represents the header of the block, which contains PreviousHash pointing to the parent block, thus linking all blocks. Nonce (The random number) in it is related to data mining. Since this project does not involve this aspect, this variable is hidden. <br/>
<br/>
BlockHeader also includes a list of transaction hashes TransactionList of all transactions in the block, stored in order. In addition, the header also contains the hash value of the Merkle tree root, which can be used to check whether the data in the block has been tampered with.<br/>
<br/>
BlockBody refers to the body of the block, which contains the corresponding transaction content for that block. BlockHash refers to the hash value of the block, which changes with changes in the block content. It is computed based on BlockHeader and BlockBody using the SHA256 algorithm.<br/>
#### Storage layer (databse) design
In blockchain applications, many systems use key-value pair-based file systems to store their data states. Because key-value pair databases often have efficient query processing performance, systems like Hyperledger Fabric and Ethereum employ key-value pair databases to store their data. Common blockchain key-value pair storage systems include LevelDB, RocksDB, and others.<br/>

RocksDB, like LevelDB, is an embedded NoSQL database. Unlike common standalone databases like MySQL or Oracle that require separate processes for deployment and startup, these databases run in the same process as blockchain nodes, starting and stopping concurrently. Users do not perceive their presence because they operate as dynamic or static dependent libraries. RocksDB is an optimized version of LevelDB, maintained by Facebook and open-source. It offers significant improvements in read and write performance compared to LevelDB. Therefore, RocksDB is used as the storage layer in this project.
### Traditional blockchain query method
<img width="500" alt="blocksearch" src="pics/oldArchitecture.png">
The diagram illustrates the structure of a blockchain. A blockchain is a linked list-like structure composed of blocks connected by hash values. The current indexing structure of the blockchain supports only relatively simple queries and only supports queries based on the unique identifier, the hash value.<br/>

In the linked list structure of the blockchain, querying a particular data in the worst-case scenario requires traversing the entire chain of blocks to find it, while in the best-case scenario, accessing the latest block is sufficient for the query. Therefore, its time complexity is O(N), where N is the number of blocks.<br/>

After finding the block, it is necessary to traverse the leaf nodes of the Merkle tree in the block body.<br/>
### Encryption Algorithm
#### SHA256 Algorithm
SHA（Secure Hash Algorithm）The characteristic of this algorithm is that a small change in data will result in an unpredictable large change in the hash value. The hash value is used to represent a fixed-size unique value for a large amount of data, and the size of the hash value for the SHA256 algorithm is 256 bits. The reason for choosing SHA256 is because its size is just right. On one hand, the probability of generating duplicate hash values is very low, and on the other hand, in the actual application process of blockchain, a large number of blocks may be generated, resulting in a large amount of information. Therefore, a size of 256 bits is appropriate.
#### ECDSA
In the transaction process of this application, the Elliptic Curve algorithm is utilized for encrypting and decrypting digital signatures.
> The Elliptic Curve Digital Signature Algorithm (ECDSA) is a public-key encryption algorithm based on elliptic curve cryptography. In 1985, Koblitz and Miller transplanted the digital signature algorithm to elliptic curves, thus giving birth to the Elliptic Curve Digital Signature Algorithm.
### Bloom Filter
A Bloom Filter is a probabilistic data structure used to determine whether an element is present in a set. It was proposed by Bloom and is widely used in scenarios such as caching and preventing malicious websites in the network domain.<br/>
<img width="420" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/0c95d3b0-3fd1-4c9b-af71-fc4c54820383">

A Bloom Filter is essentially a combination of a binary vector and a set of hash functions. It is used to represent a set. When an element is added to the set, it is hashed into several integers, and then the positions corresponding to these integers on the vector are marked as 1. When retrieving an element, the element is hashed into several integers, and then the positions corresponding to these integers on the vector are checked to see if they are all 1. If so, the element may be in the set; otherwise, it can be determined that the element is not in the set.
### Redis
Redis is a popular open-source, high-performance key-value database commonly used for caching. Its advantages include speed, simplicity, high availability, support for various data structures, and in-memory storage for fast read/write operations. It's widely used in real-time data processing scenarios like message queues and session management. Redis offers a simple API and flexible expiration policies for efficient cache management.
## 2. Storage optimization
### Storage layer
### Storage strategy
1. Cache submitted data <br/>
Users need some time for data submission to be added to the blockchain. Therefore, caching is used to store the data submitted by users. Once the data is successfully added to the blockchain, it is released from the cache.
2. Cache query data<br/>
When adding each transaction, maintain the transaction's hash and its timestamp as a sorted set. In the sorted set, each value is associated with a score. Redis can use the timestamp as a score to sort the data, achieving a time-based statistical effect. Additionally, this sorted set can be used for quick indexing to locate blocks during data queries.
3. To query data, first pass through a Bloom Filter:<br/>
This prevents cache penetration, where malicious requests query a large amount of non-existent data, causing cache to be penetrated and continuously searching the database, thereby reducing throughput and system response rate.
4. Cache block indexes<br/>
<img width="439" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/abf8c8c2-242f-4c18-8a09-f96112c1ee4b"><br/>
The block index is a balanced binary search tree (BST), with the timestamp of each block serving as the comparison key for building the tree. Each node of the tree stores the index of the corresponding block. When given a transaction timestamp, one can find the block number containing the transaction in this index.
5. Optimize the merkel tree nodes<br/>
Unlike the traditional Merkle tree structure, in this system, each non-leaf node of the Merkle tree maintains a Bloom filter. This filter is used to mark transactions reachable from that node's leaf nodes. Therefore, when searching for transactions in this Merkle tree (with n nodes), the time efficiency is optimized to O(log n) through pruning, which is the order of magnitude of the Merkle tree height. <br/>
<img width="408" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/c138eadf-498d-4231-9c24-a2812f003a21">

<img width="447" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/4ed4ab49-23f7-4725-94c4-f74de43b97b9"> <br/>
<img width="400" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/f97181c5-168b-457d-8a76-d1b313f24e1e"> <br/>
6. Optimize the lined data structure of Blockchain<br/>
Starting from the blockchain's linked structure, upon application startup, the blocks are loaded into memory, and the linked structure of blocks is loaded into an array structure. The linked list structure is transformed into an array structure, where each item in the array stores a "block" object, and abstract "links" are established based on the Previous Hash and Current Hash in the block header. The concept of "chain" is abstracted. Since both the Previous Hash and Current Hash are calculated based on block data, this approach also ensures the immutability of the data (by checking the validity of the blockchain before modifying it). <br/>
<img width="336" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/1c93333d-e028-487e-a7fe-c0529cc59aca"><br/>By converting the underlying blockchain structure to an array structure, the time complexity of accessing any block in the blockchain changes from O(N) to O(1), optimizing the query performance of the blockchain and enabling the ability to randomly access any block.
## 3. Implementation
<img width="408" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/902fee44-47ce-4caf-8c5f-a11a51348fae">

<img width="447" alt="image" src="https://github.com/xxxtrbl/blockchain_optimization/assets/68135556/23db7970-80d3-433d-8db7-f3312eb9fc2a">

   
