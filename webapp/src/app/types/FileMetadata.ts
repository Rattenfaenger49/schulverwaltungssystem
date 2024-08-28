import {FileCategory} from "./enums/FileCategory";

export type FileUploadMetadata = {
    id: number;
    fileCategory: FileCategory;
}

export type FileMetadata = FileUploadMetadata&{

    fileName: string;
    size: number;
    entityId: number;
    url: string;
    uplaodedBy: string;
    uploadedAt: string;
    fileType: string;


}

