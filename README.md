# Guestbook
[![Built with Spacemacs](https://cdn.rawgit.com/syl20bnr/spacemacs/442d025779da2f62fc86c2082703697714db6514/assets/spacemacs-badge.svg)](http://spacemacs.org)

small app where you can post messages and they will show on the screen as a list. Also tested the db which was cool.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 

Then start the client-side server if you want to change the source code and see it auto-rebuild

    lein cljsbuild auto
    
## Testing

To start the test runner

`lein test`
