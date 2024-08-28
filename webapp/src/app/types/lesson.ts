import {Address} from "./address";
import {Student} from "./student";
import {Teacher} from "./teacher";
import {ModuleType} from "./enums/module-type";
import {ContractType} from "./enums/ContractType";
import {FileMetadata} from "./FileMetadata";
import {StudentsLesson} from "./StudentsLesson";

export type Lesson = {
    id: number;
    startAt: string;
    units: number;
    modulType: ModuleType;
    lessonType: string;
    contractType: ContractType;
    description: string;
    comment: string;
    studentsLesson	: StudentsLesson[];
    teacher: Teacher;
    files: FileMetadata[];
    isSigned: boolean;
}
export type LessonVersion = {
    id: number;
    lessonId: number;
    modulType: string;
    startAt: string;  // ISO date string
    units: number;
    lessonType: string;
    description: string;
    comment: string;
    contractType: string;
    studentsIds: string;  // Comma-separated student IDs
    teacherId: number;
    signaturesIds: string;  // Comma-separated signature IDs
    createdAt: string;  // ISO date string
    createdBy: string;
    studentNames: string;  // Comma-separated student names
    teacherName: string;  // Comma-separated student names
}


