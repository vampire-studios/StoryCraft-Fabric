/*
 * Decompiled with CFR 0.149.
 */
package io.github.paradoxicalblock.storycraft.pathing;

public class PathStep {
    private final PathingNode node;
    private final PathStep parentStep;
    private int totalPathDistance;
    private int distanceToHere = 0;
    private int heuristic;
    private PathStep previous = null;
    private PathStep nextStep = null;

    public PathStep(PathingNode node, PathStep neighbor, PathingNode target, PathStep parentStep) {
        this.node = node;
        this.heuristic = this.calcHeuristic(target, node);
        this.previous = neighbor;
        this.parentStep = parentStep;
        if (neighbor != null) {
            this.distanceToHere = this.neighborAdjacent(neighbor);
        }
        this.totalPathDistance = this.distanceToHere + this.heuristic;
    }

    public int calcManhattanDistance(PathingNode target, PathingNode here) {
        return Math.abs(target.cell.x - here.cell.x) + Math.abs(target.cell.y - here.cell.y) + Math.abs(target.cell.z - here.cell.z);
    }

    public int calcHeuristic(PathingNode target, PathingNode here) {
        return (int)Math.sqrt(Math.pow(Math.abs(target.cell.x - here.cell.x), 2.0) + Math.pow(Math.abs(target.cell.y - here.cell.y), 2.0) + Math.pow(Math.abs(target.cell.z - here.cell.z), 2.0));
    }

    public PathStep getParentStep() {
        return this.parentStep;
    }

    public PathingNode getNode() {
        return this.node;
    }

    public PathStep getNextStep() {
        return this.nextStep;
    }

    private int neighborAdjacent(PathStep neighbor) {
        int newDist = neighbor.distanceToHere + 1;
        if (neighbor.node.cell.y != this.node.cell.y) {
            newDist += 8;
        }
        return newDist;
    }

    public boolean updateDistance(PathStep neighbor) {
        int newDist = this.neighborAdjacent(neighbor);
        if (newDist < this.distanceToHere) {
            this.distanceToHere = newDist;
            this.totalPathDistance = this.distanceToHere + this.heuristic;
            this.previous = neighbor;
            return true;
        }
        return false;
    }

    public PathStep reverseSteps() {
        PathStep step = this;
        while (step.previous != null) {
            step.previous.nextStep = step;
            step = step.previous;
        }
        return step;
    }

    public int getDistanceToHere() {
        return this.distanceToHere;
    }

    public int getTotalPathDistance() {
        return this.totalPathDistance;
    }

    public boolean equals(Object other) {
        if (!(other instanceof PathStep)) {
            return false;
        }
        PathStep otherStep = (PathStep)other;
        return this.node == otherStep.node;
    }
}

