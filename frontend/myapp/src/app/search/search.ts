import { Component, OnInit } from '@angular/core';
import { NgIf,CommonModule} from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Authservice } from '../service/authservice';

@Component({
  selector: 'app-search',
  imports: [NgIf, CommonModule],
  templateUrl: './search.html',
  styleUrl: './search.scss'
})
export class Search implements OnInit{

  username: string = '';
  searchResults: any[] = [];

  constructor(private route: ActivatedRoute, private authService: Authservice, private router : Router) {}

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.username = params['username'] || '';
      if(this.username){
        this.authService.search(this.username).subscribe({
         next: (res) => {
          this.searchResults = [...res]
        },
        error: (err) => {
        }
      });
      }
    });
  }

  viewAccount(id: any) {
    if (id == this.authService.getDataFromToken().userId) {
      this.router.navigate(['/profile']);
    } else {
      this.router.navigate(['/profile'], { queryParams: { id: id } });
    }
  }

}
