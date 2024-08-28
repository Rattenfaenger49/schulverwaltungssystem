import {Admin} from "./admin";
import {Student} from "./student";


export type Teacher = Admin & {
    education: string;
    qualifications: string;
    singleLessonCost: number;
    groupLessonCost: number;
    taxId: string;
    students: Student[];

}
