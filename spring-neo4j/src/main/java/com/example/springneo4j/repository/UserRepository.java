package com.example.springneo4j.repository;

import com.example.springneo4j.entity.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Neo4jRepository<UserNode, String> {

    Optional<UserNode> findByUserId(String userId);

    @Query("""
            MATCH (u:User {userId: $userId})
            
            // Step 1: Find direct friends (1st degree) in ANY direction
            OPTIONAL MATCH (u)-[:FRIEND_WITH]-(directFriend:User)
            WITH u, collect(DISTINCT directFriend) AS directFriends
            
            // Step 2: Find 2nd and 4th degree connections
            MATCH (u)-[:FRIEND_WITH*2..4]-(candidate:User)
            
            // Step 3: Filter out direct friends and self
            WHERE u <> candidate
              AND NOT candidate IN directFriends
            
            RETURN DISTINCT candidate
            LIMIT 20
            """)
    List<UserNode> findFriendRecommendations(String userId);

    @Query("""
            MATCH (u:User {userId: $userId})-[:FRIEND_WITH]-(f:User)-[:LIKES]->(p:Post)
            RETURN DISTINCT p
            """)
    List<Object> findPostsLikedByFriends(String userId);
}


/*

Neo4j Cypher Query Breakdown: Friend Recommendation AlgorithmQuery Source CodeCypher MATCH (u:User {userId: $userId})

// Step 1: Find direct friends (1st degree) in ANY direction
OPTIONAL MATCH (u)-[:FRIEND_WITH]-(directFriend:User)
WITH u, collect(DISTINCT directFriend) AS directFriends

// Step 2: Find 2nd and 3rd degree connections
MATCH (u)-[:FRIEND_WITH*2..3]-(candidate:User)

// Step 3: Filter out direct friends and self
WHERE u <> candidate
  AND NOT candidate IN directFriends

RETURN DISTINCT candidate
LIMIT 20
1. OverviewThis query generates friend recommendations for a given user by searching 2 to 3 degrees of separation (Friends of Friends, and Friends of Friends of Friends) in a social graph, while strictly excluding existing direct friends and the user themselves.2. Clause-by-Clause Execution PlanStep 0: Anchor Node LookupCypher CodePurposeMATCH (u:User {userId: $userId})Locates the target user in the graph based on the provided parameter $userId. Assigns the node to variable u.Step 1: Gather 1st-Degree Connections (Direct Friends)Cypher CodePurposeOPTIONAL MATCH (u)-[:FRIEND_WITH]-(directFriend:User)Traverses 1 hop in any direction (<->) to find direct friends. OPTIONAL MATCH acts like a SQL LEFT JOIN, ensuring the query doesn't fail if the user has 0 friends.WITH u, collect(DISTINCT directFriend) AS directFriendsAggregates all direct friend nodes into a single array named directFriends and passes it along with u to the next step.Step 2: Graph Traversal (2nd & 3rd Hops)Cypher CodePurposeMATCH (u)-[:FRIEND_WITH*2..3]-(candidate:User)Uses variable-length path traversal (*2..3) to search outward across FRIEND_WITH edges.• 2 Hops: Friends of Friends• 3 Hops: Friends of Friends of FriendsStep 3: Candidate FilteringCypher CodePurposeWHERE u <> candidatePrevents cyclic paths that loop back to the starting user.AND NOT candidate IN directFriendsEvaluates the candidate against the array collected in Step 1 to ensure existing friends are excluded, even if reached via a longer path.Step 4: Projection & Result FormattingCypher CodePurposeRETURN DISTINCT candidateDeduplicates candidate nodes that were reached through multiple path variations.LIMIT 20Caps the result set size to optimize query performance and payload size.3. Visual Example ExecutionNetwork Structure$$\text{You (u)} \longleftrightarrow \text{Bob} \longleftrightarrow \text{Charlie} \longleftrightarrow \text{David}$$Execution Pipeline[Target User] ──> You (u)
                     │
                     ├── Step 1: Collect directFriends ──> [Bob]
                     │
                     └── Step 2: Traverse *2..3 Hops
                           ├── 2 Hops (You ➔ Bob ➔ Charlie)   ──> Candidate: Charlie
                           └── 3 Hops (You ➔ Bob ➔ Charlie ➔ David) ──> Candidate: David
                     │
                     └── Step 3: Apply Filters
                           ├── Charlie (Not You AND Not in [Bob]) ──> KEEP
                           └── David   (Not You AND Not in [Bob]) ──> KEEP
                     │
                     └── Step 4: Result ──> [Charlie, David]

 */
