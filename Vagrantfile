# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure(2) do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://atlas.hashicorp.com/search.
#  config.vm.box = "sinergi/centos-65-x64"
  config.vm.box = "lmika/centos-65-x64"

  config.ssh.insert_key = false

  # The DB services
  config.vm.define "db" do |db|
    db.vm.hostname = "owdev-db"
    
    db.vm.network "forwarded_port", id: "ssh", guest: 22, host: 2221, host_ip: "localhost"
    db.vm.network "forwarded_port", guest: 5432, host: 8062, host_ip: "localhost"
    db.vm.network "private_network", ip: "192.168.54.101"

    db.vm.provision "shell", :path => "./resources/vagrant/provisioning/provision-db.sh"
  end

  # The data services
  config.vm.define "data" do |data|
    data.vm.hostname = "owdev-data"

    data.vm.network "forwarded_port", id: "ssh", guest: 22, host: 2222, host_ip: "localhost"
    data.vm.network "forwarded_port", guest: 8180, host: 8061, host_ip: "localhost"
    data.vm.network "forwarded_port", guest: 8787, host: 8067, host_ip: "localhost"
    data.vm.network "private_network", ip: "192.168.54.102"

    data.vm.provision "shell", :path => "./resources/vagrant/provisioning/provision-data.sh"

    data.vm.provider "virtualbox" do |v|
      v.memory = 2048
    end
  end

  # The portal services
  config.vm.define "portals" do |portals|
    portals.vm.hostname = "owdev-portals"

    portals.vm.network "forwarded_port", id: "ssh", guest: 22, host: 2223, host_ip: "localhost"
    portals.vm.network "forwarded_port", guest: 8080, host: 8060
    portals.vm.network "forwarded_port", guest: 8000, host: 8068, host_ip: "localhost"
    portals.vm.network "private_network", ip: "192.168.54.103"

    portals.vm.provision "shell", :path => "./resources/vagrant/provisioning/provision-portals.sh"

    portals.vm.provider "virtualbox" do |v|
      v.memory = 1024
    end
  end

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  # config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # config.vm.network "forwarded_port", guest: 80, host: 8080

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  # config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
  #   vb.memory = "1024"
  # end
  #
  # View the documentation for the provider you are using for more
  # information on available options.

  # Define a Vagrant Push strategy for pushing to Atlas. Other push strategies
  # such as FTP and Heroku are also available. See the documentation at
  # https://docs.vagrantup.com/v2/push/atlas.html for more information.
  # config.push.define "atlas" do |push|
  #   push.app = "YOUR_ATLAS_USERNAME/YOUR_APPLICATION_NAME"
  # end

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   sudo apt-get update
  #   sudo apt-get install -y apache2
  # SHELL
end
