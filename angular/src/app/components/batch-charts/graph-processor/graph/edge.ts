import {Node} from "./node";
export class Edge {

private updateStatusMap = new Map([
    ["same", "same"],
    ["deleted", "deleted"],
    ["inserted", "inserted"]
]); 
private predecessor: string;
private successor: string;
private predecessorNode: Node;
private successorNode: Node;
private edgeInCycle: boolean;
private updateStatus: string;

constructor(predecessor:string, successor: string, predecessorNode: Node, successorNode: Node){
this.predecessor = predecessor;
this.successor = successor;
this.predecessorNode = predecessorNode;
this.successorNode = successorNode;
this.updateStatus = this.updateStatusMap.get("same");
this.edgeInCycle = false;
}




public setUpdateStatusTo(status:string){
this.updateStatus = this.updateStatusMap.get(status);
}

public getSuccessor(): string{
return this.successor;

}

public getPredecessor(): string{
return this.predecessor;

}

public getSuccessorNode(){
    return this.successorNode;
}

public getPredecessorNode(){
return this.predecessorNode;

}

public getUpdateStatus(): string {
    return this.updateStatus;
}

public isTheSameEdge(edge: Edge): boolean{
    return this.predecessor === edge.getPredecessor() && this.successor === edge.getSuccessor();
}
public isTheSameEdgePerSuccessorPredecessor(predecessor:string, successor:string): boolean{
    return this.predecessor === predecessor && this.successor === successor;
}

public isEdgeInCycle(): boolean{
    return this.edgeInCycle;
}

public setEdgeInCycle(inCycle: boolean){
    this.edgeInCycle = inCycle;
}

public toString = () : string => {
    return `Edge (predecessor: ${this.predecessor}, successor: ${this.successor})`;
}

  
  }