import {Component, inject, input, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FormErrorDirective} from "../../../_services/directivs/form-error-directive";
import {CustomPageableTableComponent} from "../custom-pageable-table/custom-pageable-table.component";
import {
  CdkCell,
  CdkCellDef,
  CdkColumnDef,
  CdkHeaderCell,
  CdkHeaderCellDef,
  CdkHeaderRow, CdkHeaderRowDef, CdkRow, CdkRowDef,
  CdkTable
} from "@angular/cdk/table";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatInput} from "@angular/material/input";
import {provideMomentDateAdapter} from "@angular/material-moment-adapter";
import {TooltipModule} from "ngx-bootstrap/tooltip";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {ContractComponent} from "../../contracts/contract/contract.component";
import {MatIcon} from "@angular/material/icon";
import {MatTab, MatTabContent, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {DocumentationComponent} from "./documentation/documentation.component";
import {InvoiceComponent} from "./invoice/invoice.component";
import {PersonalComponent} from "./personal/personal.component";
import {Person} from "../../../types/person";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {UrlParamServiceService} from "../../../_services/url-param-service.service";


export const MY_FORMATS = {
  parse: {
    dateInput: 'MM/YYYY',
  },
  display: {
    dateInput: 'MM/YYYY',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },

};
@Component({
  selector: 'ys-document-page',
  standalone: true,
  providers: [
    // Moment can be provided globally to your app by adding `provideMomentDateAdapter`
    // to your app config. We provide it at the component level here, due to limitations
    // of our example generation script.
    provideMomentDateAdapter(MY_FORMATS),
  ],
  imports: [
    MatFormFieldModule,
    FormErrorDirective,
    ReactiveFormsModule,
    CustomPageableTableComponent,
    CdkTable,
    CdkColumnDef,
    CdkHeaderCell,
    CdkHeaderCellDef,
    CdkCellDef,
    CdkCell,
    CdkHeaderRow,
    CdkRow,
    CdkHeaderRowDef,
    CdkRowDef,
    MatLabel,
    MatFormField,
    MatDatepickerInput,
    MatInput,
    MatDatepickerToggle,
    MatDatepicker,
    FormsModule,
    TooltipModule,
    MatOption,
    MatSelect,
    ContractComponent,
    MatIcon,
    MatTab,
    MatTabContent,
    MatTabGroup,
    MatTabLabel,
    DocumentationComponent,
    InvoiceComponent,
    PersonalComponent
  ],
  templateUrl: './ducument-page.component.html',
  styleUrl: './ducument-page.component.scss'
})
export class DucumentPageComponent{
  user = input.required<Person>();
  userType = input.required<string>();
  params = signal<Params>([]);
  urlParamService = inject(UrlParamServiceService);
  
  constructor(    private router: Router,
                  private route: ActivatedRoute,) {
    
    this.route.queryParams.subscribe(p =>{
      this.params.set(p);
    });
    
  }

}

