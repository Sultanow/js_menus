export class BatchGraphMaker{

// NODE OBJECT { id: key, name: value.name, label: value.shortName, inCycle: false, updateStatus: "same" }
//EDGE OBJECT { source: e.predecessor, target: e.successor, inCycle: false, updateStatus: "same", type: 'Next -->>' }
private graphDataOverall: any= {
    inited: false,
    nodes: []
  };

private graphDataWithDifference: any= {
    inited: false,
    nodes: [],
    links: []
  };

private graphDataNormal: any = {
    inited: false,
    nodes: [],
    links: []
  };

  private onlyCyclesOfGraph = {
    inited: false,
    nodes: [],
    links: []
  };

  constructor(graphData: any){
    this.graphDataOverall = graphData;
    this.setUpDifferenceGraph();
    this.setUpGraphNormal();
    this.setUpOnlyCycles();
  }


  public getDifferenceGraph(): any{
      return this.graphDataWithDifference;
  }

  public getNormalGraph(): any{
    return this.graphDataNormal;
}

public getGraphCycles(): any{
    return this.onlyCyclesOfGraph;
}

  private setUpDifferenceGraph(){
      console.log("Setting up difference graph!")
      this.graphDataWithDifference = this.graphDataOverall;
  }

  private setUpGraphNormal(){
    console.log("Setting up normal graph!")
        this.graphDataOverall.nodes.forEach(element => {
            if(element.updateStatus === "inserted"){
               return;
            }
            this.graphDataNormal.nodes.push(element);
            
        });
        this.graphDataOverall.links.forEach(element => {
            if(element.updateStatus === "inserted"){
                return;
            }
            this.graphDataNormal.links.push(element);
            
        });
        this.graphDataNormal.inited = true;
  }

  private setUpOnlyCycles(){
    console.log("Setting up graph cycles!")
    this.graphDataOverall.nodes.forEach(element => {
        if(element.inCycle){
            this.onlyCyclesOfGraph.nodes.push(element);
        }
        
    });
    this.graphDataOverall.links.forEach(element => {
        if(element.inCycle){
            this.onlyCyclesOfGraph.links.push(element);
        }
        
    });
    this.onlyCyclesOfGraph.inited = true;
  }




}