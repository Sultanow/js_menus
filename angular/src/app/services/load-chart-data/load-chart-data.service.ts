import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadChartDataService {

  constructor(private http: HttpClient) {}

  private messageSource = new BehaviorSubject<any>(null);
  chartMessage = this.messageSource.asObservable();

  nextMessage(message: any) {
    this.messageSource.next(message)
    let obs = this.messageSource.observers;
    console.log(obs);
  }


  public async getGraphData(batchPath: string, pathGraphPath: string) {
    let graph = {
      inited: false,
      nodes: [],
      links: []
    };
    console.log("starting to get graph data")
    if (!graph.inited) {
      console.log("fetching data");
      let dataBatches = [];
      let dataBatchGraph = [];
      try{
        dataBatches = await this.getNodes(batchPath);
        dataBatchGraph = await this.getEdges(pathGraphPath);
      } catch(error){
        console.log("No data retrieved for graph to compare!")
      }
      graph = this.constructGraph(dataBatches,dataBatchGraph)
    }
    console.log("Sending graph data over for visualisation!")
    return graph;
  }

  private async getNodes(fileName: string) {
    console.log("Fetching nodes from " + fileName+".csv");
    const respNodes = await this.http.get(`/assets/${fileName}.csv`, { responseType: 'text' }).toPromise();
    const list = respNodes.split('\n');
    let nodes = [];
    let counter = 0;
    list.forEach((e, i) => {
      counter++;
      if (i > 0) {
        var parts = e.split(',');
        var batch = { id: parts[0], name: parts[1], shortName: parts[2],inCycle: false, updateStatus: "same"  };
        if (batch.id != "") {
          nodes.push(batch);
        }
      }
    });
    console.log(counter + " nodes processed!")
    return nodes;
  }

  private async getEdges(fileName: string) {
    console.log("Fetching edges from " + fileName+".csv");
    const respEdges = await this.http.get(`/assets/${fileName}.csv`, { responseType: 'text' }).toPromise();
    const list = respEdges.split('\n');
    let edges = [];
    let counter = 0;
    list.forEach((e, i) => {
      counter++;
      if (i > 0) {
        var parts = e.split(','); if (parts.length > 1) {
          var edge = { predecessor: parts[0].trim(), successor: parts[1].trim(),inCycle: false, updateStatus: "same" };
          edges.push(edge);
        }
      }
    });
    console.log(counter + " edges processed!")
    return edges;
  }

      private constructGraph(currentNodes: any[], currentEdges: any[]): any {
        console.log("Constructing a graph!")
        let outputGraph = {
          inited: false,
          nodes: [],
          links: []
        };
        // Strucuture of node objects -> id, name, label (shortName of Batch Job), inCycle (is node a part of cycle), updatedStatus (has node been updated, deleted or not changed at all)
        // inCycle and updateStatus will be set respectively later in batch-charts.component before being handed over for visualisation
        currentNodes.forEach(node => {
          outputGraph.nodes.push({ id: node.id, name: node.name, label: node.shortName, inCycle: false, updateStatus: "same" });
        });
        // Strucuture of edge objects -> surce (predecessor), target(successor), type, inCycle (is edge a part of cycle), updatedStatus (has edge been updated, deleted or not changed at all)
        // inCycle and updateStatus will be set respectively later in batch-charts.component before being handed over for visualisation
        currentEdges.forEach((e) => {
          outputGraph.links.push({ source: e.predecessor, target: e.successor, inCycle: false, updateStatus: "same", type: 'Next -->>' });
        });
    
        outputGraph.inited=true;
        return outputGraph;
      }

}
