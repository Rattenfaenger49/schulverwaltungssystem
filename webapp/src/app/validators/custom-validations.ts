import {AbstractControl, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";

export const ValidationPatterns = {
  phoneNumber: /^[+]?[0-9()\s/-]+$/,
  email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, // Regular expression for email validation

};
export function regexValidator(regex: RegExp): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    const valid = regex.test(control.value);
    return valid ? null : { invalidCurrency: { value: control.value } };
  };
}

export function digitsInput(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    //if there is no input then it will be valid!
    // case required is needed in case field is required
    if(control.value === null || control.value === undefined || control.value === '') {
      return null;
    }
    const valid = /^\d+$/.test(control.value);
    return valid ? null : { invalidDigit: { value: control.value }
    };
    
  };
}
export function numberInput(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    //if there is no input then it will be valid!
    // case required is needed in case field is required
    if(control.value === null || control.value === undefined || control.value === '') {
      return null;
    }
    const valid = /^(\d*[.,])?\d+(e[-+]?\d+)?$/i.test(control.value);
    return valid ? null : { invalidNumber: { value: control.value }
    };
    
  };
}
export function halfOrWholeNumberValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const value = control.value;
    
    // Allow null or empty values for optional fields
    if (value === null || value === undefined || value === '') {
      return null;
    }
    
    // Define the regular expression
    const valid = /^\d+(\.5)?$/.test(value);
    
    // Return validation result
    // 'Ungültige Nummer. Muss eine Ganzzahl oder eine halbe Ganzzahl sein.'
    return valid ? null : { invalidHalbWwholeNumber: { value: control.value } };
  };
}
export function alphanumInput(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    //if there is no input then it will be valid!
    // case required is needed in case field is required
    if(control.value === null || control.value === undefined || control.value === '') {
      return null;
    }
    const valid =  /^[A-Za-z äöüßÄÖÜ0-9.]+$/i.test(control.value);
    return valid ? null : { invalidInputAlphanum: { value: control.value }
    };
    
  };
}

export function alphaInput(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    //if there is no input then it will be valid!
    // case required is needed in case field is required
    if(control.value === null || control.value === undefined || control.value === '') {
      return null;
    }
    const valid =  /^[A-Za-z .äöüßÄÖÜ-]+$/.test(control.value);
    return valid ? null : { invalidInputAlpha: { value: control.value }
    };
    
  };
}
export function exactLength(length: number): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    //if there is no input then it will be valid!
    // case required is needed in case field is required
    if(control.value === null || control.value === undefined || control.value === '') {
      return null;
    }
    const valid = control.value.toString().length === length;
    return valid ? null : { invalidNumberLength: { value: control.value, requiredLength: length  } };
    
  };
}

export function allOrNothingValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const formGroup = control as FormGroup;
    const values = formGroup.value;
    const keys = Object.keys(values);
    
    // Check if any field is filled
    const anyFieldFilled = keys.some(key => values[key] != null && values[key] !== '');
    
    // If any field is filled, ensure all fields are filled
    if (anyFieldFilled) {
      const allFieldsFilled = keys.every(key => values[key] != null && values[key] !== '');
      return allFieldsFilled ? null : { allOrNothing: 'All fields must be filled if any field is provided' };
    }
    
    // If no field is filled, return null
    return null;
  };
}
