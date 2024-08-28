
export enum LessonDuration {
	MINUTES_45= "45 min",
	MINUTES_60 = "60 min"
}



export const LessonDurationKV = Object.entries(LessonDuration).map(([key, value]) => ({ key, value }));
