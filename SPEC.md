Requirements
===

Model
---
  Topic Tree
  * Race
    * Team
      * Car
        
  ### Race
  Details on which 'map' should be displayed in the GUI, leaderboard, start time?
  
  ### Team
  Which teams are participating in this race?
  
  ### Car
  The car / driver, which will include things like current speed, fuel remaining, position on track, laps completed, etc. This is the main bulk of the realtime stuff.
  
  
   _Notes: do we want to do the leaderboard logic and stuff server side? We could include this in the 'Race' topic_
   
   
Tech
---
We need a webserver to feed us the frontend parts, and some frontend parts to be fed.

We also need a backend to publish the data and generate the topic tree.

I think we decided on backend=Java, frontend=JS. Probably makes sense to have the backend serve the pages too so we don't have too much going on.
