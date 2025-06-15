{
  description = "A Nix-flake-based GraalVM development environment";

  inputs.nixpkgs.url = "https://flakehub.com/f/NixOS/nixpkgs/0.1";

  outputs = { self, nixpkgs }:
    let
      # A list of systems supported by this flake.
      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];

      # A helper function to generate outputs for each supported system.
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        # Import nixpkgs for the given system and apply our custom overlay.
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ self.overlays.default ];
        };
      });
    in
    {
      # This overlay customizes the nixpkgs package set.
      overlays.default = final: prev: {
        # Define our desired GraalVM package. `graalvm-ce` is the Community Edition.
        # This package provides the JDK and the `native-image` tool.
        # `graalvmPackages` is a set within nixpkgs that contains GraalVM-related packages.
        graalvm = prev.graalvmPackages.graalvm-ce;

        # For convenience and compatibility with tools that expect a standard `jdk`
        # package, we'll alias our `graalvm` package to `jdk`.
        jdk = final.graalvm;

        # Override common Java build tools to use GraalVM as their JDK.
        maven = prev.maven.override { jdk_headless = final.graalvm; };
        gradle = prev.gradle.override { java = final.graalvm; };
      };

      # The primary output of this flake is a development shell.
      devShells = forEachSupportedSystem ({ pkgs }: {
        default = pkgs.mkShell {
          # These are the packages that will be available in the development shell.
          packages = with pkgs; [
            # The GraalVM JDK, provided via our overlay.
            jdk

            # Build tools configured to use our GraalVM.
            maven
            gradle

            # Native dependencies often required by GraalVM's `native-image` utility
            # to compile native executables.
            zlib
            gcc
            ncurses
            patchelf
          ];

          # This hook runs when you enter the shell.
          shellHook = ''
            # A message to confirm that the GraalVM environment is active.
            echo ""
            echo "✅ GraalVM development environment loaded."
            echo ""
            echo "Java version:"
            java --version | sed 's/^/  /'
            echo ""
            echo "Native Image utility:"
            native-image --version | sed 's/^/  /'
            echo ""
          '';
        };
      });
    };
}
