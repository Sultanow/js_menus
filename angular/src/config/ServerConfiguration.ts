import { ENVCONFIG } from 'src/app/model/evntreetable';
import { Node } from 'src/app/components/treetable/treetable.module';

export class ServerConfiguration {

    static readonly SERVICE_URL = "/helidon";

    static readonly EVN_ITEMS: string[] = [ "item1", "item2" ];
    static readonly ENV_LIST: string[] = [ "dev1", "dev2", "dev3", "dev4", "dev5" ];
    static readonly EVN_COMPARE: string = "dev1, dev2, dev3, dev4, dev5";

    static readonly EMPTY_TREE: Node<ENVCONFIG>[] = [
        {
            value: {
                configname: "Top1"
            },
            children: [
                {
                    value: {
                        configname: "Level2 - 1"
                    },
                    children: [
                        {
                            value: {
                                configname: "Level3 - 1"
                            },
                            children: [
                            ]
                        },
                        {
                            value: {
                                configname: "Level 3 - 2"
                            },
                            children: [
                            ]
                        }
                    ]
                },
                {
                    value: {
                        configname: "Level2 - 2"
                    },
                    children: [
                    ]
                },
                {
                    value: {
                        configname: "Level2 - 3"
                    },
                    children: [
                    ]
                },
                {
                    value: {
                        configname: "Level2 - 4"
                    },
                    children: [
                    ]
                }
            ]
        },
        {
            value: {
                configname: "Top2"
            },
            children: [
                {
                    value: {
                        configname: "Level2 - 5"
                    },
                    children: [
                        {
                            value: {
                                configname: "Level3 - 3"
                            },
                            children: [
                            ]
                        }
                    ]
                }
            ]
        },
        {
            value: {
                configname: "Top3"
            },
            children: [
                {
                    value: {
                        configname: "Level2 - 6"
                    },
                    children: [
                    ]
                }
            ]
        }
    ];


}
