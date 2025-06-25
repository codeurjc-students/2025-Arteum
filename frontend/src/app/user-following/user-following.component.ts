import { Component, OnInit } from '@angular/core';
import { UsersService } from '../api-client/api/users.service';

@Component({
  selector: 'app-user-following',
  templateUrl: './user-following.component.html',
  styleUrls: ['./user-following.component.css']
})
export class UserFollowingComponent implements OnInit {
  userName: string = '';
  followingEmpty: boolean = true;
  range: string = '';
  totalFollowing: number = 0;
  sortCreatedAt: boolean = true;
  sortNameAsc: boolean = false;
  sortName: boolean = false;
  followingPage: any[] = [];
  previousPage: number | null = null;
  sort: string = 'createdAt';
  currentPage: number = 1;
  nextPage: number | null = null;
  pageSize: number = 12;

  constructor(private usersService: UsersService) {}

  ngOnInit(): void {
    this.loadFollowing();
  }

  loadFollowing(): void {
    this.usersService.getUserFollowing(this.currentPage - 1, this.pageSize, this.sort).subscribe({
      next: (following) => {
        this.followingPage = following || [];
        this.totalFollowing = this.followingPage.length;
        this.range = this.followingPage.length > 0 ? `1-${this.followingPage.length}` : '0';
        this.followingEmpty = this.followingPage.length === 0;
        this.previousPage = this.currentPage > 1 ? this.currentPage - 1 : null;
        this.nextPage = this.followingPage.length === this.pageSize ? this.currentPage + 1 : null;
      },
      error: () => {
        this.followingPage = [];
        this.followingEmpty = true;
      }
    });
  }

  onSortChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.sort = select.value;
    this.sortCreatedAt = this.sort === 'createdAt';
    this.sortNameAsc = this.sort === 'nameAsc';
    this.sortName = this.sort === 'nameDesc';
    this.currentPage = 1;
    this.loadFollowing();
  }
}
