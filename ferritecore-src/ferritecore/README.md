# FerriteCore (Judgment Client)

A MacePvP-oriented cheat client for Minecraft, created as a learning experience.

## ⚠️ Disclaimer

This project was created **purely for educational purposes** – both for me while developing it and for others who want to learn from my (admittedly crappy, but functional) code. Use responsibly and at your own risk.

## About

FerriteCore (externally named Judgment Client) is a Fabric mod designed for MacePvP gameplay. It was built as a personal project to learn more about performant mixin modding, hidden to the server gameplay modifications, and game mechanics.

## Version Compatibility

- **Primary Target:** Minecraft 1.21.11 (Fabric)
- **Backward Compatibility:** Works on versions 1.21.5 and later

### Running on Older Versions (1.21.5 - 1.21.10)

To use this client on versions prior to 1.21.11, you'll need to remove the Spear-related items array in `Utils.java`'s `findSlotWithSpear()` method, as Spears were not available in those versions.

## Authentication System

The client includes an optional authentication system that is **disabled by default**.
You just need to press Login/Activate in the Authentication module without typing anything in.

To enable authentication:
1. Host your own auth server (source code available at: [KittyAuth](https://github.com/pog5/kittyauth))
2. Set `AUTH_ENABLED = true` in `AuthClient.java`
3. Update `SERVNER_URL` to point to your auth server
4. Replace `TRUSTED_SERVER_SIGN_PUBKEY` with your auth server's public key (displayed at auth server startup)

## Modules

For a complete list of all modules with detailed descriptions, options, and default values, see [MODULES.md](MODULES.md).

## Building

```bash
./gradlew build
```

The built JAR will be located in `build/libs/`.

## License

This project is released under the [Unlicense](LICENSE) – it's in the public domain. Do whatever you want with it.

## Contributing

This project is being archived soon, so contributions are not expected. However, feel free to fork it and make it your own!

---

*Remember: Don't expect enterprise level production-quality code – but hopefully you can learn something from it!*
