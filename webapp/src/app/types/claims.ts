export interface Claims {
    authorities: string;
    roles: string;
    exp: number;
    firstName: string;
    iat: number;
    id: number;
    iss: string;
    lastName: string;
    sub: string;
    tenantId: string;
    username: string;
    verified: boolean;
}
