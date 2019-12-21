(ns interaction.core)
(require '[clojure.string :as str])

;;Welcome to my game! Please read the file "Game description" in the root index if you need.




    (def initial-map {:reality {:desc "Here is your bedroom.The diary on your desk can be used to modify game settings.\nAfter setting your character, you can sleep on the bed to start the game."
                                :desc-light "Here is your bedroom.The diary on your desk can be used to modify game settings.\nAfter setting your character, you can sleep on the bed to start the game."
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
                                :content '(:chandelier :bowl :table-in-middle-room :the-first-note :the-second-note)
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
                                :content '(:stockpot :knife :gas-stove)
                                :illumination "bright"
                                 }
                      :worship-room {:desc "This room looks like a place for prayering. At the front of the room is a statue of a scary creature which is similar to an elephant."
                                     :desc-light "This room looks like a place for prayering. At the front of the room is a statue of a scary creature which is similar to an elephant."
                                :title "In the worship room"
                                :dir {:north :middle-room}
                                :content '(:statue)
                                :illumination "faint"
                                      }
                      :library {:desc "This room is surrounded by several bookshelves, thus here may be a library. \n There is a table in the middle of the room and a candle on the top of the table.
                                The faint light of the candle makes this room bright enough to look for books on the shelves."
                                :desc-light "This room is surrounded by several bookshelves, thus here may be a library. \n There is a table in the middle of the room and a candle on the top of the table.
                                The faint light of the candle makes this room bright enough to look for books on the shelves."
                                :title "In the library"
                                :dir {:east :middle-room}
                                :content '(:bookshelves :table-in-library :candle)
                                :illumination "dark"
                                 }
                      :slave-room {:desc "This room is too dark to see anything."
                                   :desc-light-first "This is an empty room except a girl wearing white robe with a pistol in her hand... \nThe south of this room seems to have another room."
                                   :desc-light "The room where the girl was. The south of this room seems to have another room"
                                :seen-girl false
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
                             :hp 15
                             :atk 10
                             :def 10
                             :seen #{:reality}
                             :SAN 40
                             :sixth-sense 40
                             :crazy 30
                             :time-limit 100
                             :lose-sanity false
                             :seen-book false
                             :game-not-started true
                             })

    (def initial-slave {
                         :location :slave-room
                         :inventory '(:pistol)
                         :exist true
                         :ate-soup false
                         :hp 10
                         :follow false
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
                               :atk 5
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
                               :desc
            "This is the only book related the soup. Something sticky is on its cover and some of it sticks to your hand \n Most of the pages are blank, and the words you can only find is:\n
             The room in the middle: cannot leave before eating the soup\n
             The room in the north: the seasoning and plates are there. Some remaining soup is also there\n
             The room in the east: a cute child is waiting you and she has something good\n
             The room in the west: books are very precious so do not take them away, but the candle is not.\n
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
                               :desc "It is a corpse with no head on it, with lots of blood stains on it. \nThere is a note near the corpse."
                               :name "corpse"
                               :portable false
                               :inside '(:the-fifth-note)
                               :inside-secret '()
                               :SAN-check 6}

                         :the-fifth-note {
                               :desc "The note says:\n She has no name and is your faithful servant\n She will do anything she can for you, even she is not willing to\n Please love her and take care of her
          \n\nPossible operations you can use on this girl:\n
           'order follow-me': the girl with act together with you\n
           'order stay-here': the girl will wait you in current room\n
           'order give-me-pistol': the girl will give the pistol to you\n
           'order eat-soup': tell the girl to eat the strange soup\n
           'order answer-question': the girl will answer you some very simple questions.\n
           'where is the girl':tell you the current location of the girl"
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

                         :plate-carrying-poison {
                               :desc "A silver plate with poison on it."
                               :name "plate-carrying-poison"
                               :portable true
                               :inside '(:poison)
                               :inside-secret '()
                               :SAN-check 0}
                         })

    (def initial-hunting-horrors {
                               :hp 100
                               :atk 45
                               :def 20
                               :status "normal"
                               :exist true
                               :SAN-check 10})

    (def initial-slime {
                               :hp 50
                               :atk 20
                               :def 10
                               :status "normal"
                               :exist true
                               :SAN-check 8})

; (defn in? [coll elem]
;         (some #(= elem %) coll))
;
; (defn removeRepeat [coll1 result]
;         (into '() (into '() (remove #(in? coll1 %) result))))

(defn canonicalize
  "Given an input string, strip out whitespaces, lowercase all words, and convert to a vector of keywords."
  [input]
  (if (= input "") [:what]
    (remove #(= "" %) (vec (map keyword (str/split (str/replace (str/lower-case input) #"[.!?]" "") #" ")))))
  )

(defn SAN-check [state n]
  (let [random (+ (rand-int 99) 1)]
    (do (println (str/join ["You are shocked by what you see and your sanity is damaged. \nAs the result of SAN check, the number on the dice is " (str random)]))
      (if (<= random (get-in state [:data :adventurer :SAN])) (do (println "Fortunately, though you felt an indescribable fear in an instant, \nyour mind escaped from that nightmare \nYour SAN only decresed by 1") [(assoc-in state [:data :adventurer :SAN] (- (get-in state [:data :adventurer :SAN]) 1))])
        (let [reduceAmount (+ (rand-int (- n 1)) 1)]
          (do (println (str/join ["Unfortunately, your failed to move your eyes away from the thing infront of you\n Your sanity is heavily damaged and you losed " (str reduceAmount) " SAN"])) [(assoc-in state [:data :adventurer :SAN] (- (get-in state [:data :adventurer :SAN]) reduceAmount))]))))
    )
  )

(defn badend3 [state]
  (do (println "Though you fight bravely, a human can never be an opponent of a mythical creature, and you are killed in the battle...\n---BAD END3\n\n\n\n") [state :gameover]))

(defn battle [state enemy]
  (loop [plhp (get-in state [:data :adventurer :hp])
         enemyhp (get-in state [:data enemy :hp])
         number 0]
    (cond (<= plhp 0) (badend3 state)
          (<= enemyhp 0) (do (println "Your brave fighting finally break down your enemy! You win the battle!") [(assoc-in (assoc-in state [:data :adventurer :hp] plhp) [:data enemy :exist] false)])
          :else (if (= number 0)
                  (let [damage-factor (+ (rand-int 99) 1)]
                    (cond
                      (> damage-factor 75) (let [damage (* (max 1 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 2)]
                                             (do (println (str/join ["You use all of your power and cause a critical hit to the monster and cause " (str damage) " points damage"])) (recur plhp (- enemyhp damage) 1)))
                      (> damage-factor 10) (let [damage (* (max 1 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 1)]
                                             (do (println (str/join ["You attack the monster with almost all of your strength and cause " (str damage) " points damage"])) (recur plhp (- enemyhp damage) 1)))
                      :else (let [damage (/ (max 0 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 2)]
                                             (do (println (str/join ["When you rush to the monster, your balance collapse and only cause " (str damage) " points damage"])) (recur plhp (- enemyhp damage) 1)))))

                  (let [damage-factor (+ (rand-int 99) 1)]
                    (cond
                      (> damage-factor 90) (let [damage (* (max 1 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 2)]
                                             (do (println (str/join ["The monster hits you heavily and causes a critical hit which is " (str damage) " points damage"])) (recur (- plhp damage) enemyhp 0)))
                      (> damage-factor 10) (let [damage (* (max 1 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 1)]
                                             (do (println (str/join ["The monster attacks you and causes " (str damage) " points damage"])) (recur (- plhp damage) enemyhp 0)))
                      :else (let [damage (/ (max 0 (- (get-in state [:data :adventurer :atk]) (get-in state [:data enemy :def]))) 2)]
                                             (do (println (str/join ["The monster's attack does not aim at you well and only causes " (str damage) " points damage"])) (recur (- plhp damage) enemyhp 0)))))
          )
    )))

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
    (if (or (contains? (set (get-in state [:data :adventurer :inventory])) :candle) (contains? (set (get-in state [:data :map location :content])) :candle)) (if (and (= location :slave-room) (not (get-in state [:data :map :slave-room :seen-girl])))
        [(assoc-in state [:data :map :slave-room :seen-girl] true) (get-in state [:data :map location :desc-light-first])]
        [state (get-in state [:data :map location :desc-light])])
      [state (get-in state [:data :map location :desc])])
    )
  )

(defn examine [state itemname]
  (let [near-items (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])))]

      (if (contains? near-items itemname) (do (println (get-in state [:data :items itemname :desc])) (cond
                                                                                                        (= itemname :statue) [(assoc-in (nth (SAN-check state 3) 0) [:data :map :worship-room :content] '(:statue :slate))]
                                                                                                        (= itemname :the-dream-of-soup) [(assoc-in (assoc-in state [:data :adventurer :seen-book] true) [:data :map (get-in state [:data :adventurer :location]) :content] (into '() (concat (get-in state [:data :items itemname :inside]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content]))))]
                                                                                                        (and (not= itemname :the-second-note) (> (get-in state [:data :items itemname :SAN-check]) 0))
                                                                                                           (let [location (get-in state [:data :adventurer :location])]
                                                                                                           (if (> (count (get-in state [:data :items itemname :inside])) 0)
                                                                                                             (if (get-in state [:data :items (nth (get-in state [:data :items itemname :inside]) 0) :portable]) [(assoc-in (nth (SAN-check state (get-in state [:data :items itemname :SAN-check])) 0) [:data :map location :content] (into '() (concat (get-in state [:data :items itemname :inside]) (get-in state [:data :map location :content]))))]
                                                                                                               [(nth (SAN-check state (get-in state [:data :items itemname :SAN-check])) 0)])
                                                                                                             [(nth (SAN-check state (get-in state [:data :items itemname :SAN-check])) 0)]))
                                                                                                        :else (let [location (get-in state [:data :adventurer :location])]
                                                                                                           (if (> (count (get-in state [:data :items itemname :inside])) 0)
                                                                                                             (if (get-in state [:data :items (nth (get-in state [:data :items itemname :inside]) 0) :portable]) [(assoc-in state [:data :map location :content] (into '() (concat (get-in state [:data :items itemname :inside]) (get-in state [:data :map location :content]))))]
                                                                                                               [state])
                                                                                                             [state]))))
      (do (println "The item you want to examine is not near you or invalid") [state]))
    )
  )

(defn take [state itemname]
  (let [location (get-in state [:data :adventurer :location])]
    (if (and (= (get-in state [:data :map location :illumination]) "dark") (not (contains? (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map location :content]))) :candle))) (do (println "It is too dark to take anything!") [state])
      (let [contents (get-in state [:data :map location :content])]
      (if (contains? (set contents) itemname)
        (if (and (= itemname :glass-bottle) (not (get-in state [:data :items :glass-bottle :fallen]))) (do (println "You break the shell of the bulb carefully but the glass bottle falls to the floor. Meanwhile the light of chandelier is out.")
                                         [(assoc-in (assoc-in (assoc-in state [:data :items :chandelier :inside] '()) [:data :map :middle-room :illumination] "dark")
                                                     [:data :items :glass-bottle :fallen] true)])
          (if (get-in state [:data :items itemname :portable]) (do (println "take the item")
                                                [(assoc-in (assoc-in state [:data :adventurer :inventory] (cons itemname (get-in state [:data :adventurer :inventory]))) [:data :map location :content] (remove #(= itemname %) contents))])
          (do (println "This item cannot be taken into your inventory") [state])))
        (do (println "The item you want to take is not in this room now") [state]))))))

(defn move [state direction]
  (let [location (get-in state [:data :adventurer :location])]
    (let [possible-dire (get-in state [:data :map location :dir])]

      (if (contains? possible-dire direction)
        (cond
          (and (= (get-in state [:data :map location :dir direction]) :worship-room) (get-in state [:data :hunting-horrors :exist])) (do (println "As soon as you enter the worship room, you can hear a large noise of gasp\nYou know that you have a big trouble now\nFleeing does not work when facing a mythical creature\nThe only possibility of surviving is fight...")
                                                                                                                                       (if (and (get-in state [:data :adventurer :seen-book]) (get-in state [:data :npc :follow]))
                                                                                                                                         (do (println "However, you remember that the books says if the guarding of the worship room eat something alive, it will leave...\nYou look at the girl with you, do you want to sacrifice her? --yes/no")
                                                                                                                                           (loop [input (read-line)]
                                                                                                                                             (cond
                                                                                                                                               (= input "yes") (do (println "The girl looks at you with tears in her eyes...but she still walks to the monster...\nand be eaten alive.\nThe hunting horror leaves, but the girl's blood is remaining all over the place...") (move (assoc-in (nth (SAN-check state 12) 0) [:data :hunting-horrors :exist] false) direction))
                                                                                                                                               (= input "no") (do (println "You decide to fight on your own") (battle (nth (SAN-check (assoc-in state [:data :adventurer :location] (get-in state [:data :map location :dir direction])) 10) 0) :hunting-horrors))
                                                                                                                                               :else (do (println "That is not a valid answer, enter answer again") (recur (read-line))))))
                                                                                                                                         (battle (nth (SAN-check (assoc-in state [:data :adventurer :location] (get-in state [:data :map location :dir direction])) 10) 0) :hunting-horrors)))
          (and (= location :library) (and (get-in state [:data :slime :exist]) (contains? (set (get-in state [:data :adventurer :inventory])) :the-dream-of-soup))) (do (println "Just before you leave the library, you notice that the door of liabrary is shaking increasingly fierce\nThe door first turns into a liquid and then a mass of jelly, blocking the exit.\nYou know that a fight is unavoidable") (battle (assoc-in (nth (SAN-check state 8) 0) [:data :adventurer :location] :middle-room) :slime))
          :else [(assoc-in state [:data :adventurer :location] (get-in state [:data :map location :dir direction])) (get-in state [:data :map (get-in state [:data :map location :dir direction]) :title])]
          )
      (do (println "This direction has no room can go...") [state (get-in state [:data :map (get-in state [:data :adventurer :location]) :title])]) ;;remember to print "In the ..."
    )))
  )
(defn goodend [state]
  (do (println "You ate the poisoned soup after the girl and also lost consciousness.\nAfter waking up, you find that you and the cute girl are both in your room.\nThe girl is able to talk now and tells you that she was cursed by the Old One and imprisoned in the dream world.\nShe is very grateful for your help. Both of you survived from that catastrophe, great job!") [state :gameover]))

(defn useitem [state itemname]
  (let [near-items (set (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])))]
    (if (contains? near-items itemname)
      (cond
        (= itemname :bed) (do (println "You get into bed and close your eyes. \nAfter a period of time, you feel very uncomfortable and wake up. \nYou notice that you are lying on the floor of an unfamiliar room...")
                            [(assoc-in (assoc-in state [:data :adventurer :location] :middle-room) [:data :adventurer :seen] :middle-room)])
        (= itemname :bowl) (if (contains? (set (get-in state [:data :items :bowl :inside])) :poisoned-soup) (if (get-in state [:data :npc :ate-soup]) (goodend state) (escaped state))
                             (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "Though you eat all of the thick 'soup', nothing happened except feeling disgusting") [(assoc-in state [:data :items :bowl :inside] '())])
                               (do (println "There is nothing inside the bowl.") [state])))
        (= itemname :diary) (do (println "Here you can modify character status and time limit\nSuggest time limit is about 100-200, where the number means how many instructions you can use\nThe sum of hp, atk, and def cannot go over 80\n and the sum of sixth-sense, SAN, and the difference betwee SAN and the dangerous line cannot be larger than 100") (loop [idx 0
                                   state state
                                   originalstate state]
                              (cond
                                (= idx 0) (do (do (println "Please enter your character's hp") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :hp] input) originalstate)))
                                (= idx 1) (do (do (println "Please enter your character's attack") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :atk] input) originalstate)))
                                (= idx 3) (do (do (println "Please enter your character's sixth-sense") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :sixth-sense] input) originalstate)))
                                (= idx 4) (do (do (println "Please enter your character's SAN") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :SAN] input) originalstate)))
                                (= idx 5) (do (do (println "Please enter your character's dangerous line of losing sanity") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :crazy] input) originalstate)))
                                (= idx 6) (do (do (println "Please enter the time limit") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :time-limit] input) originalstate)))
                                (= idx 2) (do (do (println "Please enter your character's defend") (print ">") (flush)) (let [input (read)] (recur (+ idx 1) (assoc-in state [:data :adventurer :def] input) originalstate)))
                                (= idx 7) (cond
                                            (> (+ (get-in state [:data :adventurer :hp]) (+ (get-in state [:data :adventurer :atk]) (get-in state [:data :adventurer :def]))) 80) (do (println "The sum of hp, atk, and def cannot go over 80! Please re-enter") (recur 0 originalstate originalstate))
                                            (> (+ (get-in state [:data :adventurer :sixth-sense]) (+ (get-in state [:data :adventurer :SAN]) (- (get-in state [:data :adventurer :SAN]) (get-in state [:data :adventurer :crazy])))) 100)
                                              (do (println "The sum of sixth-sense, SAN, and the difference betwee SAN and the dangerous line cannot be larger than 100! Please re-enter") (recur 0 originalstate originalstate))
                                            :else (do (println "Game setting finished!") [state]))
                                )))
        (or (= itemname :knife) (= itemname :pistol)) (if (not (contains? (set (get-in state [:data :adventurer :inventory])) itemname))
                              (do (println (str/join ["If you want to arm with the " (name itemname) ", you need to first  it into your inventory"])) [state])
                              (do (println (str/join ["You are equipped with the " (name itemname) " now, and your attack point increas"])) [(assoc-in state [:data :adventurer :atk] (+ (get-in state [:data :items itemname :atk]) (get-in state [:data :adventurer :atk])))]))
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
    (let [inventory (into '() (concat (get-in state [:data :adventurer :inventory]) (get-in state [:data :map (get-in state [:data :adventurer :location]) :content])))]
    (if (and (contains? (set inventory) item2) (contains? (set inventory) item1))
      (cond
        (and (= item1 :plate-carrying-poison) (= item2 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add the poison into the soup")
                                                                                                                                         (let [inventorypersonal (get-in state [:data :adventurer :inventory])]
                                                                                                                                           (let [inventory-with-new-plate (cons :plate (remove #(= :plate-carrying-poison %) inventorypersonal))]
                                                                                                                                             [(assoc-in (assoc-in state [:data :items :bowl :inside] '(:poisoned-soup)) [:data :adventurer :inventory]
                                                                                                                                                        inventory-with-new-plate)])))
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item2 :plate-carrying-poison) (= item1 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add the poison into the soup")
                                                                                                                                         (let [inventorypersonal (get-in state [:data :adventurer :inventory])]
                                                                                                                                           (let [inventory-with-new-plate (cons :plate (remove #(= :plate-carrying-poison %) inventorypersonal))]
                                                                                                                                             [(assoc-in (assoc-in state [:data :items :bowl :inside] '(:poisoned-soup)) [:data :adventurer :inventory]
                                                                                                                                                        inventory-with-new-plate)])))
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item1 :plate) (= item2 :the-dream-of-soup)) (if (contains? (set (get-in state [:data :items :the-dream-of-soup :inside])) :poison) (do (println "You scraped the black matter on the book's cover and put them in the plate")
                                                                                                                                               [(assoc-in (assoc-in state [:data :items :the-dream-of-soup :inside] '()) [:data :adventurer :inventory] (cons :plate-carrying-poison (remove #(= :plate %) (get-in state [:data :adventurer :inventory]))))])
                                                              (do (println "The black semi-liquid matter was already gone") [state]))
        (and (= item2 :plate) (= item1 :the-dream-of-soup)) (if (contains? (set (get-in state [:data :items :the-dream-of-soup :inside])) :poison) (do (println "You scraped the black matter on the book's cover and put them in the plate")
                                                                                                                                               [(assoc-in (assoc-in state [:data :items :the-dream-of-soup :inside] '()) [:data :adventurer :inventory] (cons :plate-carrying-poison (remove #(= :plate %) (get-in state [:data :adventurer :inventory]))))])
                                                              (do (println "The black semi-liquid matter is already gone") [state]))
        (and (= item1 :glass-bottle) (= item2 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in (assoc-in state [:data :items :bowl :inside] (cons :poisoned-soup '())) [:data :items :glass-bottle] '())])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item2 :glass-bottle) (= item1 :bowl)) (if (contains? (set (get-in state [:data :items :bowl :inside])) :soup) (do (println "You add some poison into the soup") [(assoc-in (assoc-in state [:data :items :bowl :inside] (cons :poisoned-soup '())) [:data :items :glass-bottle] '())])
                                                                (do (println "There is no soup in the bowl, or the soup already has poison in it") [state]))
        (and (= item1 :bowl) (= item2 :stockpot)) (cond
                                                    (not (empty? (get-in state [:data :items :bowl :inside]))) (do (println "There is some soup in the bowl and no need to add from the pot") [state])
                                                    (empty? (get-in state [:data :items :stockpot :inside])) (do (println "The stockpot is empty now") [state])
                                                    :else (do (println "You put the rest of soup in the pot to the bowl") [(assoc-in (assoc-in state [:data :items :bowl :inside] '(:soup)) [:data :items :stockpot :inside] '())])
                                                    )
        (and (= item2 :bowl) (= item1 :stockpot)) (cond
                                                    (not (empty? (get-in state [:data :items :bowl :inside]))) (do (println "There is some soup in the bowl and no need to add from the pot") [state])
                                                    (empty? (get-in state [:data :items :stockpot :inside])) (do (println "The stockpot is empty now") [state])
                                                    :else (do (println "You put the rest of soup in the pot to the bowl") [(assoc-in (assoc-in state [:data :items :bowl :inside] '(:soup)) [:data :items :stockpot :inside] '())])
                                                    )
        )
      (do (println "The items you want to combine are not in your inventory or near you") [state])
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
          (= item :chandelier) (do (println "You look at the bulb closely and notice there is something inside. You can try to take it but the bulb may be broken") [(assoc-in state [:data :map :middle-room :content] (cons :glass-bottle (get-in state [:data :map :middle-room :content])))])
          (= item :slate) (do (println "You notice that there is a note stuck between the wall and the slate!") [(assoc-in state [:data :map :worship-room :content] (cons :the-fourth-note (get-in state [:data :map :worship-room :content])))])
          (= item :gas-stove) (do (println "You find out a note beneth the stove!") [(assoc-in state [:data :map :kitchen :content] (cons :the-third-note (get-in state [:data :map :kitchen :content])))])
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

(defn where [state]
  [state (get-in state [:data :adventurer :location])])

(defn interact [state]
  (let [input (read-line)]
    (let [canon-input (set (canonicalize input))]
      (cond
        (and (contains? canon-input :you) (and (contains? canon-input :enemy) (contains? canon-input :are))) (do (println "The girl shakes her head hastily") [state])
        (and (contains? canon-input :can) (and (contains? canon-input :you) (contains? canon-input :fight))) (do (println "The girl shakes her head slightly") [state])
        (and (contains? canon-input :know) (and (contains? canon-input :how) (or (contains? canon-input :escape) (and (contains? canon-input :get) (contains? canon-input :out))))) (do (println "The girl shakes her head slightly") [state])
        (and (contains? canon-input :help) (contains? canon-input :me)) (do (println "The girl nods her head slightly") [state])
        (contains? canon-input :friend) (do (println "The girl nods her head slightly") [state])
        (and (contains? canon-input :hurt) (contains? canon-input :me)) (do (println "The girl shakes her head hastily") [state])
        :else (do (println "The girl has no reaction...Maybe you question sounds like an instruction or she just didn't understand") [state])))))

(defn order [state instruction]
  (if (or (get-in state [:data :npc :follow]) (= (get-in state [:data :npc :location]) (get-in state [:data :adventurer :location])))
    (cond
    (= instruction :give-me-pistol) (if (contains? (set (get-in state [:data :npc :inventory])) :pistol)
                                      (do (println "The girl gives the pistol in her hand to you") [(assoc-in (assoc-in state [:data :npc :inventory] '()) [:data :adventurer :inventory] (cons :pistol (get-in state [:data :adventurer :inventory])))])
                                      (do (println "She already gives you the pistol") [state]))
    (= instruction :eat-soup) (cond
                                (not (contains? (set (get-in state [:data :adventurer :inventory])) :bowl)) (do (println "You do not have the bowl with you now") [state])
                                (contains? (set (get-in state [:data :items :bowl :inside])) :soup)
                                  (do (println "Though the stench of the soup makes the girl feel nausea, she still eat all of the soup in the bowl,\nbut nothing special happened")
                                    [(assoc-in state [:data :items :bowl :inside] '())])
                                (contains? (set (get-in state [:data :items :bowl :inside])) :poisoned-soup)
                                  (do (println "Though the stench of the soup makes the girl feel nausea, she still eat all of the soup in the bowl.\nSuddenly, she looks sick heavily and falls to the ground and lose the consciousness")
                                    [(assoc-in (assoc-in state [:data :items :bowl :inside] '()) [:data :npc :ate-soup] true)])
                                :else (do (println "The bowl is empty") [state]))
    (= instruction :stay-here) (do (println "The girl nods slightly and stay in the room you are now") [(assoc-in (assoc-in state [:data :npc :follow] false) [:data :npc :location] (get-in state [:data :adventurer :location]))])
    (= instruction :follow-me) (do (println "The girl nods slightly and grabs the hem of your cloth") [(assoc-in state [:data :npc :follow] true)])
    (= instruction :answer-question) (do (println "The gilr looks at you and is waiting for your question") (interact state))
    :else (do (println "The girl tiltes her head slightly, seems like that she didn't understand what you say") [state])
    )
    (do (println "The girl is not in the same room with you") [state])))

(defn operations [state]
  (do (println
      "Display most of the possible operations\n
        'look around': check the room you are in and the items you can directly see.\n
        'examine x': look at the item x closely. If there is something hide inside item x, after examine it you can access it direcly.\n
        'move x': move towards a certain direction, both successful movement and failed ones will tell you where you are now.\n
        'use x': some items have some special functions. By this operation you can equip weapon or use consumbles.\n
        'check inventory': operation used for show you what is in your inventory.\n
        'combine a b': a few pair items can be combined together to generate a new item, and they are critical for clear this game.\n
        'take x': pick up an item which is portable and can be directly accessed by you.\n
        'drop x': drop a certain item from your inventory. It will be left in the current room.\n
        'check items in this room': use this operation to see what items can be directly accessed by you in this room.\n
        'sixth-sense x': some important items may hide inside other items, which can not be found with 'examine'. \n
          With 'sixth-sense' operation you may find such a hiding item, the possiblity you can find it depends on one of the adventurer's status: sixth-sense\n
          The thing player should know is that the feedbacks of 'sixth-sense' are same for failing to find the item and where has no hiding item.\n
        'show status': show status like hp and atk to player.\n
        'where am i': display player's current location.\n\n
        IMPORTANT: when entering the name of items, please enter dash line instead of blank '(the-first-note)'\n
        "
      )
    [state]))

(defn girl-location [state]
 (if (get-in state [:data :npc :follow])
   (do (println "She is in the same room with you, and she will follow your movement") [state])
   (do (println (str/join ["She is in " (name (get-in state [:data :npc :location])) " now."])) [state])))

(def initial-env [[:look :around] look-around [:examine "@"] examine [:move "@"] move [:use "@"] useitem [:check :inventory] check-inventory [:combine "@" "@"] combine [:take "@"] take [:drop "@"] dropitem [:check :items :in :this :room] check-items-in-this-room [:sixth-sense "@"] sixth-sense [:show :status] status [:where :am :i] where [:order "@"] order [:where :is :the :girl] girl-location [:operations] operations])




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


(defn badend []
  (println "Suddenly, all lights is gone, the whole room falls into totally dark.\n There is a loud sound comes from the south room. \n With a sound of breaking, the Old Ones enter the room you are and stand in front of you \nYou can only see its blood red eyes in the dark \nand next second, you are catched by it\nCompanied by your scream, you are crashed by Chaugnar Faugn.\n ---BAD END \n\n\n\n Thank you for playing this game and see you next time!"))

(defn badend2 []
  (println "After seeing so many horrible and disgusting stuff, your psyche is totally destoried.\nAfter losing your sanity, you cannot do anything correctly anymore\nWithout the hope of escaping, you just stay in the room until the Old One come in and take your life...\n ---BAD END2 \n\n\n\n Thank you for playing this game and see you next time!"))

(defn repl
  [env data]
  (do (println "\n\n\nWelcome to Call of Cthulhu RPG - poinsoned soup. \nThe content of the story is inspired by the tabletop RPG with the same name. \nPlease enter 'go' to start the game")
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
                (and (or (= input "go") (= input :go)) (get-in state-weak [:data :adventurer :game-not-started])) (do (println "One day after work, you comeback to your home and is going to sleep. \nUnfortunatley, a horrible ancient evil is now looking for a human to be its new toy...\nRemember to use diary to change the settings before start!\n The default character is very weak!")
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

(defn main
  "Start the REPL with the initial environment."
  []
  (repl initial-env initial-data)
  )
