

export type Page<T> = {
    content:T[];
    totalPages: number;  // Total number of pages
    totalElements: number;  // Total number of items across all pages
    size: number;   // Number of items per page
    number: number;  // Current page number
    first: boolean;  // Is this the first page?
    last: boolean;   // Is this the last page?
    empty: boolean;  // Is this page empty?
}
