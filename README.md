# Minecord
A robust Discord bot written in JDA for various Minecraft functions.
Invite: https://goo.gl/Zh81Gb

### Conventions
Feel free to contribute **to the dev branch** with whatever you like, but make sure to follow these conventions.
1. Your code blocks should look like the one below. This is Java, not C++.
```
public static void main(String[] args) throws Exception {
	cl = Thread.currentThread().getContextClassLoader();
	load(args);
}
```
2. PLEASE comment all of your code so that people can find their way around it. You don't have to be excessive, just enough so that it is understandable.
3. Do not touch the version value in Bot class or the default elevated users list.
4. Be very careful when editing the Main, Loader, and Bot classes. They can break very easily.
5. REMEMBER TO NOT UPLOAD YOUR BOT TOKEN! If you upload it, people can PERMANENTLY gain access to your bot!
