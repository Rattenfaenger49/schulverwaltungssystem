


export type Trace =  {
    timestamp: string;
    timeTaken: number;
    request: {
        method: string;
        remoteAddress: string;
        uri: string;
        headers: { [key: string]: string };
    };
    response: {
        status: number;
        headers: { [key: string]: string };
    };
}
