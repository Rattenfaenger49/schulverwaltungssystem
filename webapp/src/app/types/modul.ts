import {ModuleType} from "./enums/module-type";
import {Contract} from "./contract";

export type Modul = {
    id: number;
    modulType: ModuleType;
    units: number;
    contract: Contract;
    lessonDuration: string;
    singleLessonCost: number;
    groupLessonCost: number;
    singleLessonAllowed: boolean;
    groupLessonAllowed: boolean;

}

