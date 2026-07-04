import { Component, signal, computed } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterModule } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { AdminNav } from './admin/admin-nav/admin-nav';
import { NgIf } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  imports: [Navbar, RouterOutlet, NgIf, AdminNav, RouterModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('myapp');
  protected readonly theme = signal<'light' | 'dark'>('light');
  protected currentUrl = signal<string>('');
  protected isRootRoute = computed(() => this.currentUrl() === '/');

  constructor(private router: Router) {
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null;
    const prefersDark = typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches;
    const activeTheme = savedTheme || (prefersDark ? 'dark' : 'light');

    this.theme.set(activeTheme);
    this.applyTheme(activeTheme);

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentUrl.set(event.urlAfterRedirects);
      });
  }

  toggleTheme() {
    const nextTheme = this.theme() === 'dark' ? 'light' : 'dark';
    this.theme.set(nextTheme);
    this.applyTheme(nextTheme);
  }

  private applyTheme(theme: 'light' | 'dark') {
    document.documentElement.dataset['theme'] = theme;
    localStorage.setItem('theme', theme);
  }

  isAdmin(): boolean {
    const role = localStorage.getItem('role');
    return role === 'Admin';
  }
}
