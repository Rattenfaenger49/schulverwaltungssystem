export enum InfoMessageType {
   PRIMARY= "alert-primary",
   SECONDARY = "alert-secondary",
   SUCCESS = "alert-success",
   DANGER = "alert-danger",
   WARNING = "alert-warning",
   INFO = "alert-info",
   LIGHT = "alert-light",
   DARK = "alert-dark"


}
export const InfoMessageTypeClasses: { [key in InfoMessageType]: string } = {
   [InfoMessageType.PRIMARY]: 'alert-primary',
   [InfoMessageType.SECONDARY]: 'alert-secondary',
   [InfoMessageType.SUCCESS]: 'alert-success',
   [InfoMessageType.DANGER]: 'alert-danger',
   [InfoMessageType.WARNING]: 'alert-warning',
   [InfoMessageType.INFO]: 'alert-info',
   [InfoMessageType.LIGHT]: 'alert-light',
   [InfoMessageType.DARK]: 'alert-dark'
};
