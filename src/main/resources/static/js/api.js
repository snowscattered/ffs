class API {
    static baseURL = "/";

    static Upload(file) {
        var obj;
        if(file.files.length<=0) {
            return obj = {"fileName": "error", "code": -1, "message": "error"};
        }
        var formData=new FormData();
        formData.append("file",file.files[0])
        $.ajax({
            type: "POST",
            url:this.baseURL + "upload",
            async: false,
            data: formData,
            timeout: 2000,
            dataType: "json",
            cache: false,
            processData: false,
            contentType: false,
            success: function (data) {
                obj=data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else {
                    obj = {"fileName": "error", "code": -1, "message": "error"}
                }
            }
        })
        return obj;
    }



    static getUser(uid, name, role, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid,
                    "name": name,
                    "role": role,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid,
                "name": name,
                "role": role,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addUser(user) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "other": user
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static updateUser(user, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/update",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "other": user,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/update",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "other": user,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteUser(uid, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid,
                }),
                timeout: 2000,
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            })
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/delete",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }



    static getProduct(pid, uid, name, callback, args) {
        if(callback !== null && callback !== undefined)
        {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/get",
                async: false,
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid,
                    "name": name,
                    "pid": pid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return ;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                },
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid,
                "name": name,
                "pid": pid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static addProduct(product) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "product": product,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateProduct(product, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/update",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "product": product,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/update",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "product": product,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteProduct(pid, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/product/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "pid": pid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args);
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/product/delete",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "pid": pid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }


    static getOrder(oid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "oid": oid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/get",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addOrder(order, listings) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "order": order,
                "listings": listings,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateOrder(order, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/update",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "order": order,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/update",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "order": order,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteOrder(oid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/order/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "oid": oid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/order/delete",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }


    static getListing(oid, lid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/listing/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "oid": oid,
                    "lid": lid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/listing/get",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
                "lid": lid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addListing(listings) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/listing/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "listings": listings,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateListing(listing, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/listing/update",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "listing": listing,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/listing/update",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "listing": listing,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteListing(lid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/listing/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "lid": lid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/listing/delete",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "lid": lid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }


    static getReview(oid, rid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "oid": oid,
                    "rid": rid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/get",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "oid": oid,
                "rid": rid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static addReview(review) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "review": review,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            },
        })
        return obj;
    }
    static updateReview(review,callback,args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/update",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "review": review,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/update",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "review": review,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
    static deleteReview(rid, callback, args) {
        if(callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/review/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "rid": rid,
                }),
                timeout: 2000,
                dataType: "json",
                success: function (data) {
                    callback(data, args)
                },
                complete: function (XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args)
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj,args)
                    }
                }
            })
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/review/delete",
            contentType: "application/json",
            async: false,
            data: JSON.stringify({
                "token": $.cookie("token"),
                "rid": rid,
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data
            },
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

}