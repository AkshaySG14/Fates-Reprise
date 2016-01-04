package com.inoculates.fatesreprise.Events;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.inoculates.fatesreprise.Characters.*;
import com.inoculates.fatesreprise.Characters.Character;
import com.inoculates.fatesreprise.Screens.GameScreen;
import com.inoculates.fatesreprise.Storage.Storage;
import com.inoculates.fatesreprise.Text.Dialogue;

// These are the events that are fired when Daur wishes to talk to someone.
public class VillagerEvents extends Event {
    Character character;
    Storage storage;

    String message = null;
    boolean shop = false;

    public VillagerEvents(TiledMap map, GameScreen screen, Storage storage, Character character) {
        super(screen, map);
        this.character = character;
        this.storage = storage;
        startEvent();
    }

    protected void startEvent() {
        Villager villager = (Villager) character;
        // Provides the villager identifier by getting the unique ID that each villager has.
        setMessage(villager.getVillager());
    }

    // These are the individual messages that are displayed when Daur talks to a villager. This is always dependent on
    // the villager that is being spoken to.
    private void setMessage(int villager) {
        switch (villager) {
            case 0:
                // First villager, who greets Daur.
                // Note that some villagers have multiple lines. Which line is uttered is decided by the integer array
                // villager stages. Each villager has a stage that represents which dialogue it shall use.
                switch (storage.villagerStages[0]) {
                    // First dialogue. Note that immediately after speaking, the stage is incremented by the
                    // setvillagerstage method. This is so that when Daur speaks to the first villager again, a
                    // different dialogue appears. Note that the villager stage can also be changed by outside methods.
                    case 0:
                        message = "Welcome to our fair village! Hope you find it pleasant.";
                        storage.setVillagerStage(0);
                        break;
                    case 1:
                        message = "Carthell is a pretty easy place to navigate. In the center, there's a marketplace filled " +
                                "with shops. East of that is the Four Statue Square. Southwest " +
                                "of the square is Aragoth's fountain. North of the Four Statue Square is where the library is located.";
                                break;
                }
                break;
            case 1:
                switch (storage.villagerStages[1]) {
                    case 0:
                        message = "They say this fountain was built by Aragoth to grant his wife's " +
                                "wishes. But, I've never seen it do anything more than gurgle.";
                        break;
                }
                break;
            case 2:
                switch (storage.villagerStages[2]) {
                    case 0:
                        message = "I've never really been outside of the village, but I've heard " +
                                "that there's an old man with some good advice out east. But, you'll have to go through " +
                                "the swamp to reach him.";
                        break;
                }
                break;
            case 3:
                switch (storage.villagerStages[3]) {
                    case 0:
                        message = "Hello stranger. Have you checked out the local spell shop? " +
                                "It's stocked with plenty of spells for a magical adventurer such as yourself.";
                        storage.setVillagerStage(3);
                        break;
                    case 1:
                        message = "Those statues are pretty strange, aren't they?. But, they've been in the town for generations. " +
                                "I'm not just not exactly sure what they're there for...";
                        break;
                }
                break;
            case 4:
                switch (storage.villagerStages[4]) {
                    case 0:
                        message = "I tend to these flowers. Quite beautiful, aren't they? " +
                                "I've been taking care of them so long that sometimes, it almost feels as though they're speaking to me. " +
                                "Asking me to nourish them with water and fertilizer...";
                        break;
                }
                break;
            case 5:
                switch (storage.villagerStages[5]) {
                    case 0:
                        message = "Da da doo I want you. Do re mi come with me. Wait a moment. What are you doing, " +
                                "standing there gawking at me singing?";
                        storage.setVillagerStage(5);
                        break;
                    case 1:
                        message = "Da dee da doe. I want to know. Me fa so. Will you go? My singing is amazing, isn't it?";
                        break;
                }
                break;
            case 6:
                switch (storage.villagerStages[6]) {
                    case 0:
                        message = "Hey mister. If ya need anything at all, just ask me. Or you can ask Ma right there inside " +
                                "that house. She knows a lot of things as well.";
                        storage.setVillagerStage(6);
                        break;
                    case 1:
                        message = "At the spell enchantment shop, the owner will only give you the spell that makes you " +
                                "strong if you know a secret password. What that password is? I don't know. It's a secret.";
                        storage.setVillagerStage(6);
                        break;
                    case 2:
                        // Note that this message changes based on how far Daur is in the main quest.
                        if (storage.mainQuestStage < 4)
                            message = "I heard there's a magical person imprisoned to the south in Faron Woods, inside a great oak tree." +
                                    " But, you'll have to find your way there first.";
                            // The rest of the game hasn't been fleshed out yet, so this part has not been fully done (the
                            // main quest amount).
                        else
                            message = "Hmmmm. There seems to be all kinds of trouble in the east now.";
                        storage.setVillagerStage(6);
                        break;
                    case 3:
                        message = "It's said that there is a strange temple south east of the village. Dedicated to some god or the other.";
                        storage.setVillagerStage(6);
                        break;
                    case 4:
                        // Same as the above exception.
                        if (storage.mainQuestStage < 2)
                            message = "If you ever feel lost in Faron Woods, you could always consult the Fairy Queen. She knows " +
                                    "even more than I do.";
                        else
                            message = "The rugged lands to the east sure are dangerous to explore. I wouldn't go in any " +
                                    "of the caves without some kind of light.";
                        storage.setVillagerStage(6);
                        break;
                    case 5:
                        message = "When facing a skeleton, the only way to kill it for good is to light it up.";
                        storage.setVillagerStageSp(6, 1);
                        break;
                }
                break;
            case 7:
                switch (storage.villagerStages[7]) {
                    case 0:
                        message = "Running and dodging around these bushes is great for exercise. Running into them can be a bit " +
                                "frustrating though. They always slow me down. But, I've heard of a special pair of boots that allow " +
                                "the wearer to blow through everything!";
                        break;
                }
                break;
            case 8:
                switch (storage.villagerStages[8]) {
                    case 0:
                        message = "I love swimming in this pool. It's shallow, and there's no current. But, if you're into some more difficult " +
                                "swimming, I've heard there's a special pair of swimming gear somewhere that'll give you the boost necessary " +
                                "to stay afloat in choppy or deep waters.";
                        break;
                }
                break;
            case 9:
                switch (storage.villagerStages[9]) {
                    case 0:
                        message = "Hmph. You look like a wimp. Someone incapable of lifting heavy weights or pushing large " +
                                "objects. Well, my advice to you, wimp, is to get stronger. But it looks like the only way you're " +
                                "going to accomplish that is through magic. Meaning you're going to stay a wimp.";
                        break;
                }
                break;
            case 10:
                switch (storage.villagerStages[10]) {
                    case 0:
                        message = "Ah, hello and welcome to my... ahem... humble abode. I love having guests around, " +
                                "and it's always interesting to meet new faces. If you were to come back some time later " +
                                "I might have a nice present for you in return for spending some time with me.";
                        break;
                }
                break;
            case 11:
                switch (storage.villagerStages[11]) {
                    case 0:
                        message = "Not to be overly rude, but why are you in my house? Oh. I don't mind or anything like " +
                                "that..";
                        storage.setVillagerStage(11);
                        break;
                    case 1:
                        message = "Not many people visit me in this house of mine. Which is quite unfortunate for me, seeing " +
                                "as I exist almost exclusively in it. Not that there are no positives to staying home all the time. " +
                                "It's a surefire way to live a relatively safe life for example.";
                        break;
                }
                break;
            case 12:
                switch (storage.villagerStages[12]) {
                    case 0:
                        message = "Hello there. In Carthell, I'm known as the resident wise man of sorts. I've gained a heck of " +
                                "a lot of knowledge from reading these books in the local library. If you require any assistance " +
                                "don't hesitate to ask.";
                        storage.setVillagerStage(12);
                        break;
                    case 1:
                        message = "You're asking what you need to do now? How am I suppose to know that?";
                        break;
                }
                break;
            case 13:
                switch (storage.villagerStages[13]) {
                    case 0:
                        message = "Welcome to my newly renovated house! All I'm missing is one of those old, antique, " +
                                "grandfather clocks. Hmm...";
                        break;
                }
                break;
            case 14:
                switch (storage.villagerStages[14]) {
                    case 0:
                        message = "Hey there. I am the oldest of the Berluvi brothers. Glad to meet ya. If ya need to " +
                                "just hang around, come to our place any time you like. There's always interesting people " +
                                "showing up here.";
                        break;
                }
                break;
            case 15:
                switch (storage.villagerStages[15]) {
                    case 0:
                        message = "Color me surprised, a stranger. We get a lot of strangers here in Carthell, and this boarding " +
                                "house is the perfect place to house em. If anyone interesting is boarding here, I'll let you know.";
                        storage.setVillagerStage(15);
                        break;
                    case 1:
                        message = "Nope. Nobody yet.";
                        break;
                }
                break;
            case 16:
                switch (storage.villagerStages[16]) {
                    case 0:
                        message = "I am the youngest brother of the Berluvi brothers. I practise medicine, and can help you if " +
                                "you're wounded. Come back to me if you or any other person you know is wounded.";
                        storage.setVillagerStage(16);
                        break;
                }
                break;
            case 17:
                switch (storage.villagerStages[17]) {
                    case 0:
                        message = "You might be wondering why these two houses are separated. They were once joined, but " +
                                "my sister and I got into a huge fight. Afterwards, she left, only to return and cordon off a " +
                                "portion of the house. To be honest, I'm not even angry anymore, but she won't listen to reason.";
                        break;
                }
                break;
            case 18:
                switch (storage.villagerStages[18]) {
                    case 0:
                        message = "Are you here on behalf of my sister? If so, I'd advise you to leave immediately. I'm never going to move in with her " +
                                "for as long as she lives. And there's absolutely nothing you can do to convince me.";
                        break;
                }
                break;
            case 19:
                switch (storage.villagerStages[19]) {
                    case 0:
                        message = "Oh dear, are you an adventurer? If you are, would you mind doing me a favor? I've always " +
                                "wanted a copy of the book \"A Bestiary of Extraordinary Creatures\" by Melvin Shermille. If you " +
                                "could find and give it to me, I would reward you quite handsomely.";
                        storage.setVillagerStage(19);
                        break;
                    case 1:
                        message = "Did you find a copy of the book yet?";
                        break;
                }
                break;
            case 20:
                switch (storage.villagerStages[20]) {
                    case 0:
                        message = "Welcome to my shop. I recognize you as a fellow practitioner of magic. I offer a " +
                                "wide variety of spells that any competent mage could use. Of course, these would not be " +
                                "as potent as the ancient ones you may find in the wild, but they are powerful nevertheless.";
                        storage.setVillagerStage(20);
                        break;
                    case 1:
                        message = "Take a look at my collection.";
                        shop = true;
                        storage.setVillagerStage(20);
                        break;
                    case 2:
                        message = "Remember that spells are only as powerful as the user who wields them.";
                        shop = false;
                        storage.setVillagerStageSp(20, 1);
                        break;

                }
                break;
            case 21:
                switch (storage.villagerStages[21]) {
                    case 0:
                        message = "Keep it down friend. I run here a very tight business. What kind of business you ask? Quite " +
                                "simple. This is a potion shop, which offers a wide variety of potent elixers and potions that will " +
                                "enhance your natural abilities, or give you new powers. If you need anything, just ask.";
                        storage.setVillagerStage(21);
                        break;
                    case 1:
                        message = "Feel free to peruse at your leisure.";
                        shop = true;
                        storage.setVillagerStage(21);
                        break;
                    case 2:
                        message = "Enjoy...";
                        shop = false;
                        storage.setVillagerStageSp(21, 1);
                        break;
                }
                break;
            case 22:
                switch (storage.villagerStages[22]) {
                    case 0:
                        message = "Hello there. I am known around Carthell as a junk dealer of sorts. My wares consist of pretty " +
                                "much random garbage around Jaril. They also change from time to time, so if you should always " +
                                "come by once in a while to see if anything interests you. Talk to me again if you wanna trade.";
                        storage.setVillagerStage(22);
                        break;
                    case 1:
                        message = "Have a look!";
                        shop = true;
                        storage.setVillagerStage(22);
                        break;
                    case 2:
                        message = "Hope you found something useful.";
                        shop = false;
                        storage.setVillagerStageSp(22, 1);
                        break;
                }
                break;
            case 23:
                switch (storage.villagerStages[23]) {
                    case 0:
                        message = "Hello. I am the mayor of this... fine town. I am quite a busy man, and I don't really have the " +
                                "time to talk with strangers. So, if you'll excuse me...";
                        break;
                }
                break;
            case 24:
                switch (storage.villagerStages[24]) {
                    case 0:
                        message = "Hi there! I'm the mayor's aide, and I'm here to answer any concerns you may have about Carthell. " +
                                "If you have any questions in the future, don't hesitate to ask me. If need be, I'll even act as a direct " +
                                "correspondent to the mayor.";
                        storage.setVillagerStage(24);
                        break;
                    case 1:
                        message = "Need anything?";
                        storage.setVillagerStage(24);
                        break;
                }
                break;
            case 25:
                switch (storage.villagerStages[26]) {
                    case 0:
                        message = "Oh, did you chance upon my son outside. He's a bright one, though he can be a handful " +
                                "at times. You say he's giving you advice? Oh dear. It must be a habit he picked up from his " +
                                "father. Well, as long as he's not making any trouble, I guess it's alright.";
                        break;
                }
                break;
            case 26:
                switch (storage.villagerStages[25]) {
                    case 0:
                        message = "Oh man. I sure would love to have an train model in my house. Wait a minute... What are " +
                                "you doing here?";
                        storage.setVillagerStage(25);
                        break;
                    case 1:
                        message ="...";
                        break;
                }
                break;
        }
        message();
    }

    protected void message() {
        switch (stage) {
            case 0:
                Dialogue dialogue = new Dialogue(screen, message, this);
                screen.setText(dialogue, dialogue.getBackground());
                screen.daur.stun();
                character.stun();
                character.setFrozen(true);
                screen.daur.forceState(0);
                break;
            case 1:
                screen.setText(null, null);
                ShopEvent events;
                if (shop)
                    events = new ShopEvent(map, screen, character);
                else {
                    screen.daur.unStun();
                    character.unStun();
                    character.setFrozen(false);
                }
        }
    }
}
