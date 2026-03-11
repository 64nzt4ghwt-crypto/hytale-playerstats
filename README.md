# PlayerStats for Hytale

Complete player statistics tracking — kills, deaths, KDR, kill streaks, playtime, blocks placed, and more.

## Commands

| Command | Description |
|---------|-------------|
| `/stats` | View your own stats |
| `/stats <player>` | View another player's stats |
| `/leaderboard` | Top 10 by kills (default) |
| `/leaderboard <category>` | Top 10 by category |

## Leaderboard Categories

`kills` · `deaths` · `kdr` · `streak` · `playtime` · `blocks` · `logins`

## Tracked Stats

| Stat | Description |
|------|-------------|
| Kills / Deaths | PvP combat tracking |
| KDR | Kill/death ratio (rounded to 2dp) |
| Best Kill Streak | Longest kill streak without dying |
| Playtime | Total time on server (h m format) |
| Blocks Placed/Broken | Build tracking |
| Logins | How many times player has joined |
| First/Last Seen | Join date and last activity |

## Features
- **Auto-save** every 5 minutes + on server shutdown
- **Persistent storage** across restarts (`stats.dat`)
- **Real-time streaks** — reset on death, tracked in session
- **Gold/Silver/Bronze** medals on leaderboards
