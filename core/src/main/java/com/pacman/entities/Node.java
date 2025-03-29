package com.pacman.entities;

import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class Node {
    public Vector2 position;  // position of this node on the grid
    public Node parent;       // parent node (used for reconstructing the path)
    public float gCost;       // distance from the start node
    public float hCost;       // estimated distance to the target (heuristic)
    public float fCost;       // total cost (gCost + hCost)

    public Node(Vector2 position, Node parent, float gCost, float hCost) {
        this.position = position;
        this.parent = parent;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return position.epsilonEquals(node.position, 0.01f); // Use epsilon to avoid floating-point precision issues
    }

    @Override
    public int hashCode() {
        return Objects.hash(position.x, position.y);
    }

}
