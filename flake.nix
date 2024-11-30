{
  description = "A dev environment for java";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }: 
    flake-utils.lib.eachDefaultSystem (system:
    let
      pkgs = nixpkgs.legacyPackages."${system}";
    in {
      devShell = pkgs.mkShell {
        packages = with pkgs; [
          kotlin
          gradle
        ];
      };

      apps = {
        build = {
          type = "app";
          program = "${pkgs.writeScript "brainfuckkt-build" ''
            #!${pkgs.bash}/bin/bash
            JAVA_HOME="${pkgs.jdk17}" ${pkgs.gradle_8}/bin/gradle build
          ''}";
        };
        test = {
          type = "app";
          program = "${pkgs.writeScript "brainfuckkt-test" ''
            #!${pkgs.bash}/bin/bash
            JAVA_HOME=${pkgs.jdk17} ${pkgs.gradle_8}/bin/gradle test
          ''}";
        };
      };
    }
  );
}
