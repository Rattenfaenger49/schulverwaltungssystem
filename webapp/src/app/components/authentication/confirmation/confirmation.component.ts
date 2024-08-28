import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AuthService} from "../../../_services/data/auth.service";
import {MatIconModule} from "@angular/material/icon";
import {UpdatePasswordComponent} from "../update-password/update-password.component";


@Component({
  selector: "ys-confirmation",
  templateUrl: "./confirmation.component.html",
  styleUrls: ["./confirmation.component.css"],
  standalone: true,
  imports: [MatIconModule, UpdatePasswordComponent],
})
export class ConfirmationComponent implements OnInit {
  token!: string;
  verificationError: string | null = null; // Set to null or a default value initially
  verificationSuccess: boolean = false;
  setPassowrd = false;
  msg = "" ;
  emailNotSend = true;
  constructor(
    private route: ActivatedRoute,
    private auth: AuthService,
  ) {
  }
  ngOnInit(): void {

    this.route.queryParams.subscribe((params) => {
      this.token = params["token"];
      this.auth.saveSchoolId(params['tenantId'] ?? '');
      
    });
    if (this.token) {
      // Call your API service to verify the token and set the password
      this.auth.verifyToken(this.token).subscribe({
        next: () => {
          this.setPassowrd = true;
        },
        error: (error) => {

          this.verificationError =
              error.error.message || "Token verification failed";
        }
      });
    }
  }

  sendNewToken() {
    this.auth.sendNewConfirmationToken(this.token).subscribe({
      next: (res) => {

        this.emailNotSend = false
        this.msg = res.message;
        setTimeout(() => {
        //  this.router.navigateByUrl("/home");
        }, 5000);
      },
      error: (res) => {

        console.error(res);
        setTimeout(() => {
          //this.router.navigateByUrl("/home");
        }, 5000);
      },
    });
  }
}
