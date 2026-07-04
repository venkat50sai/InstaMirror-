import { Component } from '@angular/core';
import {Router, NavigationEnd, RouterOutlet } from '@angular/router';
@Component({
  selector: 'app-admin',
  imports: [RouterOutlet],
  standalone: true,
  templateUrl: './admin.html',
  styleUrls: ['./admin.scss'],
})
export class Admin {

}
