(ns interaction.core)
(require '[clojure.string :as str])

;; # Interaction
;; A simple parser that can remember facts.
;;

;; # Actions
;;
;; Let an *action* be a function with a specific
;; interface: It will take in a *state* followed by
;; a sequence of arguments.  It will return a vector
;; consisting of a new state and a response string.
;;
;; Here is a simple example, a post-increment function.
;; Assume the state is a hash-map with a key :vars
;; and a value containing a hash of the variables and
;; their values.


(defn post-increment
  "Action: post-increment
   Returns the value of a variable from the state and increments it."
  [state var]
  (if-let [val (get-in state [:vars var])]
    [(update-in state [:vars var] inc) val]
    [(assoc-in state [:vars var] 1) 0]))

;; <pre><code>
;; interaction.core=> (post-increment {:vars {:x 10}} :x)
;; [{:vars {:x 11}} 10]
;; </code></pre>

;; ## Your work
;;
;; Fill in the code for these functions.
;;

(defn lookup-var
  "Given a state and a variable name, return the value of the variable
  if it has been defined, otherwise return 0."
  [state var]
  (if-let [val (get-in state [:vars var])]
    [state val]
    [state 0])
  )

;; <pre><code>
;; interaction.core=> (lookup-var {:vars {:x 10}} :x)
;; [{:vars {:x 10}} 10]
;; </code></pre>

(defn set-plus
  "Action: set-plus.  Set var = e1 + e2, return the sum as a result."
  [state var e1 e2]
    [(assoc-in state [:vars var] (+ (if (integer? e1) e1 (get-in state [:vars e1])) (if (integer? e2) e2 (get-in state [:vars e2])))) (+ (if (integer? e1) e1 (get-in state [:vars e1])) (if (integer? e2) e2 (get-in state [:vars e2])))]
  )

;; <pre><code>
;; interaction.core=> (set-plus {:vars {:x 10}} :y :x 20)
;; [{:vars {:x 10 :y 30}} 30]
;; </code></pre>

(defn set-var
  "Action: set-var. Set var = e1.  Return the new value as a result."
  [state var e1]
    [(assoc-in state [:vars var] (if (integer? e1) e1 (get-in state [:vars e1]))) (if (integer? e1) e1 (get-in state [:vars e1]))])

;; <pre><code>
;; interaction.core=> (set-var {:vars {:x 10}} :y :x)
;; [{:vars {:x 10 :y 10}} 10]
;; </code></pre>

(defn there-is-a
  "Action: there-is-a.  Remember that an object obj exists.
  Returns \"There is a obj\" as a result."
  [state object]
    [(assoc-in state [:objects object] []) (str/replace "There is a @." #"@" (str/replace (str object) #":" ""))]
  )

;; <pre><code>
;; interaction.core=> (there-is-a {:vars {:x 10}} :shoe)
;; [{:vars {:x 10 :y 10}
;;   :objects {:shoe []}} "There is a shoe."]
;; </code></pre>

(defn the-obj-is
  "Action: there-obj-a.  Remember an adjective that applies to an object.
  Returns \"The obj is adj\" as a result."
  [state object adj]
  [(assoc-in state [:objects object] (conj (get-in state [:objects object]) adj)) (str/replace (str/replace "The @ is #." #"@" (str/replace (str object) #":" "")) #"#" (str/replace (str adj) #":" ""))]
  )
;; <pre><code>
;; interaction.core=> (the-obj-is {:vars {:x 10} :objects {:shoe []}} :shoe :blue)
;; [{:vars {:x 10} :objects {:shoe [:blue]}} "The shoe is blue."]
;; </code></pre>



(defn describe-obj
  "Describe the given object \"The obj is adj\" if it exists in state . If not, return \"There is no obj\""
  [state object]
  (if (get-in state [:objects object])
    (loop [adjs (get-in state [:objects object])
         result [state]]
    (if (empty? adjs) result
      (recur (rest adjs) (conj result (str/replace (str/replace "The @ is #." #"#" (str/replace (str (first adjs)) #":" "")) #"@" (str/replace (str object) #":" ""))))))
    [state (str/replace "There is no @." #"@" (str/replace (str object) #":" ""))])
  )
;; <pre><code>
;; interaction.core=> (describe-obj  {:vars {:x 10} :objects {:shoe [:blue :big]}} :shoe)
;; [{:vars {:x 10}, :objects {:shoe [:blue :big]}} "The shoe is blue." "The shoe is big."]
;; </code></pre>


(defn forget-obj
  "Delete the given object and return \"What obj?\""
  [state object]
  [(assoc-in state [:objects] (dissoc (get-in state [:objects]) object)) (str/replace "What @?" #"@" (str/replace (str object) #":" ""))]
  )
;; <pre><code>
;; interaction.core=> (forget-obj {:objects {:show [:exciting]}} :show)
;; [{:objects {}} "What show?"]
;; </code></pre>


;; # Action Environment
;;
;; The runtime environment is a vector of the form
;;
;; ``` [ phrase action phrase action ...]```
;;
;; The "phrase" is a canonicalized vector of keywords.
;; (Remember that a keyword is a symbol starting with a colon, like :name.)
;; The `@` character will represent a variable.


    (def initial-map {:reality {:desc "Here is your bedroom.The diary on your desk can be used to modify game settings.\nAfter seting your character, you can sleep on the bed to start the game."
                                :desc-light "Here is your bedroom.The diary on your desk can be used to modify game settings.\nAfter seting your character, you can sleep on the bed to start the game."
                                :title "In your bedroom"
                                :dir {}
                                :content '(:bed :diary)
                                :illumination "bright"
                                }
                      :middle-room {:desc "This is a room with a chandelier generating faint light and grey walls. There are doors in all of the four directions. \n At the middle of the room is a table with a bowl on it."
                                :desc-light "This is a room with a chandelier generating faint light and grey walls. There are doors in all of the four directions. \n At the middle of the room is a table with a bowl on it."
                                :title "In the middle room"
                                :dir {:east :slave-room
                                      :south :worship-room
                                      :west :library
                                      :north :dining-hall}
                                :content '(:chandelier :bowl :table :the-first-note :the-second-note)
                                :illumination "faint"
                                      }
                      :dining-hall {:desc "Here seems to be a dining hall, many elctric bulbs make this room quite bright. There is a large table in the middle of the room. Tablewares on it are all made of silver.
                                    \n There is another room in the east. There is also a cupboard in the corner of the room."
                                :desc-light "Here seems to be a dining hall, many elctric bulbs make this room quite bright. There is a large table in the middle of the room. Tablewares on it are all made of silver.
                                    \n There is another room in the east. There is also a cupboard in the corner of the room."
                                :title "In the dining hall"
                                :dir {:east :kitchen :south :middle-room}
                                :content '(:cupboard :table-in-dining-hall)
                                :illumination "bright"
                                     }
                      :kitchen {:desc "This room looks like a kitchen. There is a gas stove and a large stockpot on the stove. There is also a knife on the chopping board."
                                :desc-light "This room looks like a kitchen. There is a gas stove and a large stockpot on the stove. There is also a knife on the chopping board."
                                :title "In the kitchen"
                                :dir {:west :dining-hall}
                                :content '(:stockpot :knife)
                                :illumination "bright"
                                 }
                      :worship-room {:desc "This room is too dark to see anything."
                                     :desc-light "This room looks like a place for prayering. At the front od the room is a statue of a scary creature which is similar to an elephant."
                                :title "In the worship room"
                                :dir {:north :middle-room}
                                :content '(:statue)
                                :illumination "dark"
                                      }
                      :library {:desc "This room is surrounded by several bookshelves, thus here may be a library. \n There is a table in the middle of the room and a candle on the top of the table.
                                The faint light of the candle makes this room bright enough to look for books on the shelves."
                                :desc-light "This room is surrounded by several bookshelves, thus here may be a library. \n There is a table in the middle of the room and a candle on the top of the table.
                                The faint light of the candle makes this room bright enough to look for books on the shelves."
                                :title "In the library"
                                :dir {:east :middle-room}
                                :content '(:bookshelves :table :candle)
                                :illumination "dark"
                                 }
                      :slave-room {:desc "This room is too dark to see anything."
                                   :desc-light "This is an empty room except a girl wearing white robe with a pistol in her hand... \n The south of this room seems to have another room."
                                :title "In the slave room"
                                :dir {:west :middle-room :south :cell}
                                :content '()
                                :illumination "dark"
                                    }
                      :cell {:desc "This room is too dark to see anything."
                             :desc-light "This room looks like a cell using for imprisoning someone, and there is something in the corner of the room..."
                                :title "In the cell"
                                :dir {:north :slave-room}
                                :content '(:corpse)
                                :illumination "dark"
                              }})

    (def initial-adventurer {
                              :location :reality
                             :inventory '()
                             :tick 0
                             :hp 20
                             :atk 10
                             :seen #{:reality}
                             :SAN 20
                             :sixth-sense 100
                             :crazy 20
                             :time-limit 50
                             :lose-sanity false
                             :game-not-started true
                             })

    (def initial-slave {
                         :location :slave-room
                         :inventory '(:pistol)
                         :hp 10
                         })

    (def initial-items {
                         :bed {
                               :desc "It is a comfortable bed"
                               :name "bed"
                               :portable false
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :chandelier {
                               :desc "It is an old chandelier with a quite large light bulb, but only radiating dim light."
                               :name "chandelier"
                               :portable false
                               :inside '()
                               :inside-secret '(:glass-bottle)
                               :SAN-check 0}

                         :glass-bottle {
                               :desc "This is a small glass bottle with some black semi-liquid stuff in side"
                               :name "glass bottle"
                               :portable true
                               :inside '(:poison)
                               :inside-secret '()
                               :fallen false
                               :SAN-check 0}

                         :bowl {
                               :desc "It's a bowl made of wood with a weird red and thick soup inside"
                               :name "bowl"
                               :portable true
                               :inside '(:soup)
                               :inside-secret '()
                               :SAN-check 0}

                         :soup {
                               :desc "Thick and red soup"
                               :name "soup"
                               :portable false
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :table-in-middle-room {
                               :desc "It is a common table with a bowl on it"
                               :name "table-in-middle-room"
                               :portable false
                               :inside '(:bowl)
                               :inside-secret '()
                               :SAN-check 0}

                         :table-in-dining-hall {
                               :desc "This is just a common table with some silver tablewares on it"
                               :name "table in dining hall"
                               :portable false
                               :inside '(:plate)
                               :inside-secret '()
                               :SAN-check 0}

                         :table-in-library {
                               :desc "It is a commontable with a candle on it"
                               :name "table in library"
                               :portable false
                               :inside '(:candle)
                               :inside-secret '()
                               :SAN-check 0}

                         :the-first-note {
                               :desc "The words on the note: \n If you want to escape \n Eat the soup in the bowl with poison \n You can never leave before that \n If you do not do it in 2 hours \n It will come to meet you"
                               :name "the-first-note"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :the-second-note {
                               :desc "It says:\n the room in the middle is the room where soup is\n the room on the east side is the room for slave and a cell\n the room on the south side is the worship room\n the room on the west side is a library\n the room on the north side is a dininghall and a kitchen"
                               :name "the-second-note"
                               :portable true
                               :back "The back of this note says:\n wArm SOup\n maDe oF HumAn BLoOd\n EnJOY it BeForE GeTtING CoLD"
                               :inside '()
                               :inside-secret '()
                               :SAN-check 3}

                         :cupboard {
                               :desc "It is a cupboard with lots of tablewares inside. All of them are made of silver..."
                               :name "cupboard"
                               :portable false
                               :inside '(:plate)
                               :inside-secret '()
                               :SAN-check 0}

;;                          :tablewares {
;;                                :desc
;;                                :name "tablewares"
;;                                :portable false
;;                                :inside '()}

                         :gas-stove {
                               :desc "It's a gas stove, but it is not working for the reason you do not know."
                               :name "gas-stove"
                               :portable false;
                               :inside '()
                               :inside-secret '(:the-third-note)
                               :SAN-check 0}

                         :the-third-note {
                               :desc "The note says:\n Important seasoning is out of stock now"
                               :name "the-third-note"
                               :portable true;
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :stockpot {
                               :desc "It is a huge stockpot. The staff inside is some 'soup' and a human head and some other parts of a human body. The 'soup' in the pot is still warm."
                               :name "stockpot"
                               :portable false
                               :inside '(:soup)
                               :inside-secret '()
                               :SAN-check 8}

                         :knife {
                               :desc "This is a knife made of iron"
                               :name "knife"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :statue {
                               :desc "It is a statue of a scary creature looks like an elephant. You understand subconsciously that you cannot look at it anymore and look away from it, and you find a slate beneath with words on it."
                               :name "statue"
                               :portable false
                               :inside '(:slate)
                               :inside-secret '()
                               :SAN-check 3}

                         :slate {
                               :desc "The words on the slate are a certain ancient language. You tried to interpretate it and you find out that it is talk about one of the Old Ones."
                               :name "slate"
                               :portable false
                               :inside '()
                               :inside-secret '(:the-fourth-note)
                               :SAN-check 0}

                         :the-fourth-note {
                               :desc "The words on this note says:\n Inside the faint sun\n or the dream of the red soup\n you can find the best seaoning"
                               :name "the-fourth-note"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :bookshelves {
                               :desc "Several large bookshelves stuffed with all kinds of books. You may find a book related to the soup."
                               :name "bookshelves"
                               :portable false
                               :inside '()
                               :inside-secret '(:the-dream-of-soup)
                               :SAN-check 0}

                         :the-dream-of-soup {
                               :desc "This is the only book related the soup. Something sticky is on its cover and some of it sticks to your hand \n Most of the pages are blank, and the words you can only find is:\n
                                              The room in the middle: cannot leave before eating the soup\n
                                              The room in the north: the seasoning and plates are there. Some remaining soup is also there\n
                                              The room in the east: a cute child is waiting you and she has something good\n
                                              The room in the west: books are very precious so do not take them away, but the candle is not.
                                              The room in the south: the god is sleeping here, also the important information about the poison. The gardian will not leave before eat something alive\n
                                              Most important: make a well mental preparation when you are going to eat the soup--You may die!"
                               :name "the dream of soup"
                               :portable true
                               :inside '(:poison)
                               :inside-secret '()
                               :SAN-check 3}

                         :candle {
                               :desc "It is a common candle, though its light is not so bright, it is enough for you to explore other places."
                               :name "candle"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :corpse {
                               :desc "It is a corpse with no head on it, with lots of blood stains on it."
                               :name "corpse"
                               :portable false
                               :inside '(:the-fifth-note)
                               :inside-secret '()
                               :SAN-check 6}

                         :the-fifth-note {
                               :desc "The note says:\n She has no name and is your faithful servant\n She will do anything she can for you, even she is not willing to\n Please love her and take care of her"
                               :name "the-third-note"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :plate {
                               :desc "It is a sliver plate which can be used to keep some liquid or semi-liquid stuff in it."
                               :name "plate"
                               :portable true
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :pistol {
                               :desc "It's a pistol which is powerful enough to kill a human."
                               :name "pistol"
                               :portable true
                               :atk 10
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :poisoned-soup {
                               :desc "This soup has poison in it."
                               :name "poisoned soup"
                               :portable false
                               :inside '()
                               :inside-secret '()
                               :SAN-check 0}

                         :plate-carring-poison {
                               :desc "A silver plate with poison on it."
                               :name "plate-carring-poison"
                               :portable true
                               :inside '(:poison)
                               :inside-secret '()
                               :SAN-check 0}
                         })

    (def initial-hunting-horrors {
                               :hp 50
                               :atk 30
                               :def 10
                               :status "normal"
                               :exist true})

    (def initial-slime {
                               :hp 40
                               :atk 10
                               :def 4
                               :status "normal"
                               :warning false})

; (defn in? [coll elem]
;         (some #(= elem %) coll))
;
; (defn removeRepeat [coll1 result]
;         (into '() (into '() (remove #(in? coll1 %) result))))

(defn SAN-check [state n]
  (let [random (+ (rand-int 99) 1)]
    (do (println (str/join ["You are shocked by what you see and your sanity is damaged. \nAs the result of SAN check, the number on the dice is " (str random)]))
      (if (<= random (get-in state [:data :adventurer :SAN])) (do (println "Fortunately, though you felt an indescribable fear in an instant, \nyour mind escaped from that nightmare \nYour SAN only decresed by 1") [(assoc-in state [:data :adventurer :SAN] (- (get-in state [:data :adventurer :SAN]) 1))])
        (let [reduceAmount (+ (rand-int (- n 1)) 1)]
          (do (println (str/join ["Unfortunately, your failed to move your eyes away from the thing infront of you\n Your sanity is heavily damaged and you losed " (str reduceAmount) " SAN"])) [(assoc-in state [:data :adventurer :SAN] (- (get-in state [:data :adventurer :SAN]) reduceAmount))]))))
    )
  )

;; (defn SAN-left [state]
;;   (do (str/join ["Your remaining SAN is " (get-in state [:data :adventurer :SAN]) ",\nand your dangerous line for losing sanity is " (get-in state [:data :adventurer :crazy])]) [state]))

(defn escaped [state]
  (do (println "After you eat the poisoned soup, you feel extremely sick and lose your consciousness. Later, you wake up and find that you are in your own bedroom.
           \nEverything happened in that place is too real to be considered just a dream, \nbut anyway, you are still alive!")
    [state :gameover]))

(def initial-data {:map initial-map :adventurer initial-adventurer :items initial-items :npc initial-slave :hunting-horrors initial-hunting-horrors :slime initial-slime})

(defn timeinc [state]
  (update-in state [:data :adventurer :tick] inc)
  )

(defn look-around [state]
  (let [location (get-in state [:data :adventurer :location])]
    (if (or (contains? (set (get-in state [:data :adventurer :inventory])) :candle) (contains? (set (get-in state [:data :map location :content])) :candle)) [state (get-in state [:data :map location :desc-light])]
      [state (get-in state [:data :map location :desc])])
    )
  )

(defn examine [state itemname]
  (let [near-items (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])))]

      (if (contains? near-items itemname) (do (println (get-in state [:data :items itemname :desc])) (if (and (not= itemname :the-second-note) (> (get-in state [:data :items itemname :SAN-check]) 0)) (let [location (get-in state [:data :adventurer :location])]
                                                                                                       [(assoc-in (nth (SAN-check state (get-in state [:data :items itemname :SAN-check])) 0) [:data :map location :content] (into '() (concat (get-in state [:data :items itemname :inside]) (get-in state [:data :map location :content]))))])
                                                                                                       (let [location (get-in state [:data :adventurer :location])]
                                                                                                       [(assoc-in state [:data :map location :content] (into '() (concat (get-in state [:data :items itemname :inside]) (get-in state [:data :map location :content]))))])))
      (do (println "The item you want to examine is not near you or invalid") [state]))
    )
  )

(defn pick [state itemname]
  (let [location (get-in state [:data :adventurer :location])]
    (if (and (= (get-in state [:data :map location :illumination]) "dark") (not (contains? (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map location :content]))) :candle))) (do (println "It is too dark to pick up anything!") [state])
      (let [contents (get-in state [:data :map location :content])]
      (if (contains? (set contents) itemname)
        (if (and (= itemname :glass-bottle) (not (get-in state [:data :items :glass-bottle :fallen]))) (do (println "You break the shell of the bulb carefully but the glass bottle falls to the floor. Meanwhile the light of chandelier is out.")
                                         [(assoc-in (assoc-in (assoc-in state [:data :items :chandelier :inside] '()) [:data :map :middle-room :illumination] "dark")
                                                     [:data :items :glass-bottle :fallen] true)])
          (if (get-in state [:data :items itemname :portable]) (do (println "picked up")
                                                [(assoc-in (assoc-in state [:data :adventurer :inventory] (cons itemname (get-in state [:data :adventurer :inventory]))) [:data :map location :content] (remove #(= itemname %) contents))])
          (do (println "This item cannot be picked up") [state])))
        (do (println "The item you want to pick is not in this room now") [state]))))))

(defn move [state direction]
  (let [location (get-in state [:data :adventurer :location])]
    (let [possible-dire (get-in state [:data :map location :dir])]

      (if (contains? possible-dire direction)
        (cond
;;           (and () () ) ()
;;           (and () () ) ()
          :else [(assoc-in state [:data :adventurer :location] (get-in state [:data :map location :dir direction])) (get-in state [:data :map (get-in state [:data :map location :dir direction]) :title])]
          )
      (do (println "This direction has no room can go...") [state (get-in state [:data :map (get-in state [:data :adventurer :location]) :title])]) ;;remember to print "In the ..."
    )))
  )

(defn useitem [state itemname]
  (let [near-items (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])))]
    (if (contains? near-items itemname)
      (cond
        (= itemname :bed) (do (println "You get into bed and close your eyes. \nAfter a period of time, you feel very uncomfortable and wake up. \nYou notice that you are lying on the floor of an unfamiliar room...")
                            [(assoc-in (assoc-in state [:data :adventurer :location] :middle-room) [:data :adventurer :seen] :middle-room)])
        (= itemname :bowl) (if (contains? (set (get-in state [:data :items :bowl :inside])) :poisoned-soup) (escaped state)
                             (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "Though you eat all of the thick 'soup', nothing happened except feeling disgusting") [(assoc-in state [:data :items :bowl :inside] '())])
                               (do (println "There is nothing inside the bowl.") [state])))
        (= itemname :diary) (loop [idx 0
                                   state state]
                              (cond
                                (= idx 0) (do (do (println "Please enter your character's hp") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :hp] input))))
                                (= idx 1) (do (do (println "Please enter your character's atk") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :atk] input))))
                                (= idx 2) (do (do (println "Please enter your character's sixth-sense") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :sixth-sense] input))))
                                (= idx 3) (do (do (println "Please enter your character's SAN") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :SAN] input))))
                                (= idx 4) (do (do (println "Please enter your character's dangerous line of losing sanity") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :crazy] input))))
                                (= idx 5) (do (do (println "Please enter the time limit") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :time-limit] input))))
                                (= idx 6) (do (println "Game setting finished!") [state])
                                ))
        )
      (do (println "The item you want to use is not near you or invalid") [state]))
    )
  )

(defn check-inventory [state]
  (let [inventory (get-in state [:data :adventurer :inventory])]
   (loop [idx 0]
     (if (< idx (count inventory)) (do (println (nth inventory idx)) (recur (+ idx 1)))
       [state]))))

(defn dropitem [state item]
  (let [inventory (get-in state [:data :adventurer :inventory])
        location (get-in state [:data :adventurer :location])]
    (if (contains? (set inventory) item) (do (println "The item is dropped in the current room") [(assoc-in (assoc-in state [:data :adventurer :inventory] (remove #(= item %) inventory)) [:data :map location :content] (cons item (get-in state [:data :map location :content])))])
      (do (println "The item you want to drop is not in your inventory") [state])))
  )

(defn check-items-in-this-room [state]
  (if (and (not (contains? (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content]))) :candle)) (= (get-in state [:data :map (get-in state [:data :adventurer :location]) :illumination]) "dark"))
    (do (println "It is too dark to see anything!") [state])
    (let [content (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])]
    (loop [idx 0]
      (if (< idx (count content)) (do (println (nth content idx)) (recur (+ idx 1)))
        [state]))))
  )

(defn combine [state item1 item2]
  (if (and (not (contains? (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content]))) :candle)) (= (get-in state [:data :map (get-in state [:data :adventurer :location]) :illumination]) "dark"))
    (do (println "It is too dark to combine items together!") [state])
    (let [inventory (get-in state [:data :adventurer :inventory])]
    (if (and (contains? (set inventory) item2) (contains? (set inventory) item1))
      (cond
        (and (= item1 :plate-carring-poison) (= item2 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in state [:data :items :bowl :inside] '(:poisoned-soup))])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item2 :plate-carring-poison) (= item1 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in state [:data :items :bowl :inside] '(:poisoned-soup))])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item1 :plate) (= item2 :the-dream-of-soup)) (if (contains? (set (get-in state [:data :items :the-dream-of-soup :inside])) :poison) (do (println "You scraped the black matter on the book's cover and put them in the plate")
                                                                                                                                               [(assoc-in (assoc-in state [:data :items :the-dream-of-soup :inside] '()) [:data :adventurer :inventory] (cons :plate-carring-poison (remove #(= :plate %) inventory)))])
                                                              (do (println "The black semi-liquid matter was already gone") [state]))
        (and (= item2 :plate) (= item1 :the-dream-of-soup)) (if (contains? (set (get-in state [:data :items :the-dream-of-soup :inside])) :poison) (do (println "You scraped the black matter on the book's cover and put them in the plate")
                                                                                                                                               [(assoc-in (assoc-in state [:data :items :the-dream-of-soup :inside] '()) [:data :adventurer :inventory] (cons :plate-carring-poison (remove #(= :plate %) inventory)))])
                                                              (do (println "The black semi-liquid matter is already gone") [state]))
        (and (= item1 :glass-bottle) (= item2 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in state [:data :items :bowl :inside] '(:poisoned-soup))])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item2 :glass-bottle) (= item1 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in state [:data :items :bowl :inside] '(:poisoned-soup))])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        )
      (do (println "The items you want to combine are not in your inventory") [state])
      )
    ))
  )

(defn sixth-sense [state item]
  (if (and (not (contains? (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content]))) :candle)) (= (get-in state [:data :map (get-in state [:data :adventurer :location]) :illumination]) "dark"))
    (do (println "It is too dark to see anything!") [state])
    (let [line (get-in state [:data :adventurer :sixth-sense])]
    (let [number (+ (rand-int 99) 1)]
      (if (<= number line)
        (cond
          (= item :bookshelves) (do (println "You find out the only book related to the soup with your sixth-sense!") [(assoc-in state [:data :map :library :content] (cons :the-dream-of-soup (get-in state [:data :map :library :content])))])
          (= item :chandelier) (do (println "You look at the bulb closely and notice there is something inside. You can try to pick it but the bulb may be broken") [(assoc-in state [:data :map :middle-room :content] (cons :glass-bottle (get-in state [:data :map :middle-room :content])))])
          (= item :slate) (do (println "You notice that there is a note stuck between the wall and the slate!"))
          :else (do (println "You did not find anything helpful...") [state])
          )
        (do (println "You did not find anything helpful...") [state])))))
  )

(defn status [state]
  (let [hp (get-in state [:data :adventurer :hp])
        atk (get-in state [:data :adventurer :atk])
        sixth-sense (get-in state [:data :adventurer :sixth-sense])
        SAN (get-in state [:data :adventurer :SAN])
        crazy (get-in state [:data :adventurer :crazy])]
    (do (println (str/join ["hp: " hp "  atk: " atk "  sixth-sense: " sixth-sense "  SAN: " SAN " and the dangerous line of losing sanity: " crazy]))
      [state])))

(def initial-env [[:look :around] look-around [:examine "@"] examine [:move "@"] move [:use "@"] useitem [:check :inventory] check-inventory [:combine "@" "@"] combine [:pick "@"] pick [:drop "@"] dropitem [:check :items :in :this :room] check-items-in-this-room [:sixth-sense "@"] sixth-sense [:show :status] status])
;; (def initial-env [[:postinc "@"] post-increment [:lookup "@"] lookup-var [:set "@" :to "@" :plus "@"] set-plus [:set "@" :to "@"] set-var [:there :is :a "@"] there-is-a [:the "@" :is "@"] the-obj-is [:describe "@"] describe-obj [:forget "@"] forget-obj])  ;; add your other functions here

;; # Parsing
;;
;; This is mostly code from the lecture.

(defn canonicalize
  "Given an input string, strip out whitespaces, lowercase all words, and convert to a vector of keywords."
  [input]
  (if (= input "") [:what]
    (remove #(= "" %) (vec (map keyword (str/split (str/replace (str/lower-case input) #"[.!?]" "") #" ")))))
  )

;; <pre><code>
;; interaction.core> (canonicalize "The shoe is blue.")
;; [:the :shoe :is :blue]
;; </code></pre>


;;helper
(defn correspond [input command]
  (loop [input input
         command command
         vars []]
    (cond
      (and (empty? input) (empty? command)) vars
      (or (empty? input) (empty? command)) nil
      (= (first command) "@") (recur (rest input) (rest command) (conj vars (first input)))
      (= (first command) (first input)) (recur (rest input) (rest command) vars)
;;       :else (do (println "wrong instruction") nil)
      )
    )
  )

(defn react
  "Given a state and a canonicalized input vector, search for a matching phrase and call its corresponding action.
  If there is no match, return the original state and result \"I don't know what you mean.\""
  [state input-vector]
  (if (= input-vector [:what]) [state]
    (loop [command-vec (get-in state [:runtime])
         input input-vector]

      (if (empty? command-vec) [state "Invalid instruction"]
      (let [vars (correspond input (first command-vec))]
        (if (nil? vars) (recur (rest (rest command-vec)) input)
         (apply (first (rest command-vec)) (cons state vars)))))
    ))
  )

;; <pre><code>
;; interaction.core> (react {:vars {:x 10} :runtime initial-env} [:postinc :x])
;; [ {:vars {:x 11} :runtime { ... omitted for space ... }}  10]
;; </code></pre>

(defn badend []
  (println "Suddenly, all lights is gone, the whole room falls into totally dark.\n There is a loud sound comes from the south room. \n With a sound of breaking, the Old Ones enter the room you are and stand in front of you \nYou can only see its blood red eyes in the dark \nand next second, you are catched by it\nCompanied by your scream, you are crashed by Chaugnar Faugn.\n ---BAD END \n\n\n\n Thank you for playing this game and see you next time!"))

(defn badend2 []
  (println "After seeing so many horrible and disgusting stuff, your psyche is totally destoried.\nAfter losing your sanity, you cannot do anything correctly anymore\nWithout the hope of escaping, you just stay in the room until the Old One come in and take your life...\n ---BAD END2 \n\n\n\n Thank you for playing this game and see you next time!"))

(defn repl
  [env data]
  (do (println "Welcome to Call of Cthulhu RPG - poinsoned soup. \nThe content of the story is inspired by the tabletop RPG with the same name. \nPlease enter 'go' to start the game")
    (loop [state {:runtime env :data data}]

      (cond (>= (get-in state [:data :adventurer :tick]) (get-in state [:data :adventurer :time-limit])) (badend)
        (< (get-in state [:data :adventurer :SAN]) 0) (badend2)
        :else (if (and (< (get-in state [:data :adventurer :SAN]) (get-in state [:data :adventurer :crazy])) (not (get-in state [:data :adventurer :lose-sanity])))

            (let [hp-before (get-in state [:data :adventurer :hp])
                sixth-sense-before (get-in state [:data :adventurer :sixth-sense])
                atk-before (get-in state [:data :adventurer :atk])]
                  (let [state-weak (assoc-in (assoc-in (assoc-in (assoc-in state [:data :adventurer :hp] (/ hp-before 2)) [:data :adventurer :sixth-sense] (/ sixth-sense-before 2)) [:data :adventurer :atk] (/ atk-before 2)) [:data :adventurer :lose-sanity] true)]
                    (do (println "Your all status reduced to half due to losing of sanity and a strong feeling of fear")
                      (do (print ">") (flush)
               (let [input (read-line)]
                (cond
                (= input "quit") (println "Closing the game, see you next time.")
                (and (or (= input "go") (= input :go)) (get-in state-weak [:data :adventurer :game-not-started])) (do (println "One day after work, you comeback to your home and is going to sleep. \nUnfortunatley, a horrible ancient evil is now looking for a human to be its new toy...")
                                                                                      (recur (assoc-in state-weak [:data :adventurer :game-not-started] false)))
                :else (let [result (react state-weak (canonicalize input))]
                  (if (and (> (count result) 1) (= (result 1) :gameover)) (println "Thank you for playing this game and see you next time!")
                  (do (loop [idx 1] (if (< idx (count result)) (do (println (result idx)) (recur (+ idx 1)))))
                    (recur (timeinc (nth result 0))))))))))))

            (do (print ">") (flush)
             (let [input (read-line)]
              (cond
              (= input "quit") (println "Closing the game, see you next time.")
              (and (or (= input "go") (= input :go)) (get-in state [:data :adventurer :game-not-started])) (do (println "One day after work, you comeback to your home and is going to sleep. \nUnfortunatley, a horrible ancient evil is now looking for a human to be its new toy...")
                                                                                      (recur (assoc-in state [:data :adventurer :game-not-started] false)))
              :else (let [result (react state (canonicalize input))]

                    (if (and (> (count result) 1) (= (result 1) :gameover)) (println "Thank you for playing this game and see you next time!")
                  (do (loop [idx 1] (if (< idx (count result)) (do (println (result idx)) (recur (+ idx 1)))))
                    (recur (timeinc (nth result 0))))))))))))

    ))


;; <pre><code>
;; interaction.core=> (repl initial-env)
;; Welcome!  Let's talk.
;; > there is a spoon.
;; There is a spoon. which you can try to interpretate
;; > the spoon is blue.
;; The spoon is blue.
;; > the spoon is big.
;; The spoon is big.
;; > describe the spoon.
;; The spoon is blue.
;; The spoon is big.
;; > forget the spoon.
;; What spoon?
;; > describe the spoon.
;; There is no spoon.
;; > bye
;; nil
;; interaction.core=>
;; </code></pre>

(defn main
  "Start the REPL with the initial environment."
  []
  (repl initial-env initial-data)
  )
