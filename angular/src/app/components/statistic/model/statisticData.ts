export class StatisticData {
    updateTime: string = "";
    title: string = "";
    traces: Object[];
    layout: Object;
    nextTrace: Object[];
    prevTrace: Object[];
    startDate: string = "";
    nextDate: string = "";
    prevDate: string = "";
    endDate: string = "";
}

export const DEFAULT_LAYOUT = 
{
    xaxis: {
      showline: true,
      showgrid: true,
      showticklabels: true,
      linewidth: 2,
      ticks: 'outside',
      tickfont: {
        family: 'Arial',
        size: 12,
      }
    },
    yaxis: {
      showgrid: true,
      zeroline: true,
      showline: true,
      showticklabels: true,
    },
    autosize: true,
    margin: {
      autoexpand: true,
      l: 100,
      r: 20,
      t: 110,
    },
    showlegend: true,
    title: "",
  };
