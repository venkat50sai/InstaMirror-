import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-landing',
  standalone: true,
  imports: [],
  templateUrl: './admin-landing.html',
  styleUrls: ['./admin-landing.scss'],
})
export class AdminLanding {
  constructor(private router: Router) {}

  goToHome() {
    this.router.navigate(['/admin/home']);
  }
}
